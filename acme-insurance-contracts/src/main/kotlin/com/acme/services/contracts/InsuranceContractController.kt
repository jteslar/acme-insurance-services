package com.acme.services.contracts

import com.acme.services.commons.exceptions.BadRequestException
import com.acme.services.commons.exceptions.NotFoundException
import com.acme.services.contracts.client.InsuranceFeeClient
import com.acme.services.contracts.client.ProductClient
import com.acme.services.contracts.model.InsuranceContractRequest
import com.acme.services.contracts.model.toDomain
import com.acme.services.contracts.validation.InsuranceContractValidator
import com.acme.services.fees.model.InsuranceFeeResponse
import com.acme.services.products.model.ProductResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import springfox.documentation.annotations.ApiIgnore
import javax.transaction.Transactional

@RestController
@RequestMapping("/contracts")
class InsuranceContractController {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InsuranceContractController::class.java)
    }

    @Autowired
    private lateinit var repository: InsuranceContractRepository

    @Autowired
    private lateinit var validator: InsuranceContractValidator

    @Autowired
    private lateinit var productClient: ProductClient

    @Autowired
    private lateinit var insuranceFeeClient: InsuranceFeeClient

    @Transactional
    @PostMapping("/upload")
    fun upload(
        @ApiIgnore auth: Authentication,
        @RequestPart("file") file: MultipartFile
    ) {
        logger.info("Uploading insurance contracts [file = ${file.name}]")

        if (file.contentType != "text/csv") throw BadRequestException("No valid CSV file")

        repository.deleteByUserId(auth.name.toLong())

        val products: Map<String, ProductResponse> = productClient.getAll()
        val fees: List<InsuranceFeeResponse> = insuranceFeeClient.getAll()
        val contracts: List<InsuranceContract> = file.bytes.inputStream().bufferedReader()
            .readLines()
            .drop(1)
            .toSet()
            .map { line -> validator.validate(line).apply { userId = auth.name.toLong() } }
            .map { contract -> calculateInsuranceFee(contract, products, fees) }

        repository.saveAll(contracts)

        logger.info("Insurance contracts uploaded [size = ${contracts.size}]")
    }

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(
        @ApiIgnore auth: Authentication,
        @Validated @RequestBody contract: InsuranceContractRequest
    ): InsuranceContract {
        logger.info("Creating insurance contract [$contract]")

        val product: ProductResponse = productClient.getByName(contract.productName!!)
        val fee: InsuranceFeeResponse = insuranceFeeClient.getByCategoryAndValue(product.category, contract.declaredValue!!)

        var newContract: InsuranceContract = calculateInsuranceFee(contract.toDomain().apply { userId = auth.name.toLong() }, product, fee)
        newContract = repository.save(newContract)

        logger.info("Insurance contract created [$newContract]")
        return newContract
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(
        @ApiIgnore auth: Authentication,
        @PathVariable id: Long
    ) {
        logger.info("Deleting insurance contract [id = $id]")

        val contract: InsuranceContract = repository.findByIdOrNull(id) ?: throw NotFoundException("Contract does not exist")
        if (contract.userId != auth.name.toLong()) throw NotFoundException("Contract does not exist")

        repository.deleteById(id)

        logger.info("Insurance contract deleted [id = $id]")
    }

    @GetMapping
    fun getAll(
        @ApiIgnore auth: Authentication,
        @RequestParam(required = false) product: String?
    ): List<InsuranceContract> {
        return product?.let { repository.findAllByUserIdAndProductNameContaining(auth.name.toLong(), product) }
            ?: repository.findAllByUserId(auth.name.toLong())
    }

    private fun calculateInsuranceFee(contract: InsuranceContract, products: Map<String, ProductResponse>, fees: List<InsuranceFeeResponse>): InsuranceContract {

        val product: ProductResponse = products[contract.productName]
            ?: throw BadRequestException("Product ${contract.productName} not found")

        val fee: InsuranceFeeResponse = fees.firstOrNull { fee ->
            fee.productCategory == product.category
                    && fee.limitLow <= contract.declaredValue
                    && (fee.limitTop == null || fee.limitTop!! >= contract.declaredValue)
        } ?: throw BadRequestException("Insurance fee for $contract not found")

        return calculateInsuranceFee(contract, product, fee)
    }

    private fun calculateInsuranceFee(contract: InsuranceContract, product: ProductResponse, fee: InsuranceFeeResponse): InsuranceContract {

        contract.productCategory = product.category
        contract.feePercentage = fee.feePercentage
        contract.feeAmount = (contract.declaredValue * fee.feePercentage / 100).toLong()
        return contract
    }
}