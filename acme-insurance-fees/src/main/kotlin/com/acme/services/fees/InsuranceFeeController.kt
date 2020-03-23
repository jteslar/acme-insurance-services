package com.acme.services.fees

import com.acme.services.commons.exceptions.BadRequestException
import com.acme.services.commons.exceptions.NotFoundException
import com.acme.services.fees.model.InsuranceFeeResponse
import com.acme.services.fees.validation.InsuranceFeeValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.transaction.Transactional

@RestController
@RequestMapping("/fees")
class InsuranceFeeController {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InsuranceFeeController::class.java)
    }

    @Autowired
    private lateinit var repository: InsuranceFeeRepository

    @Autowired
    private lateinit var validator: InsuranceFeeValidator

    @Transactional
    @PostMapping("/upload")
    fun upload(@RequestPart("file") file: MultipartFile) {
        logger.info("Uploading insurance fees [file = ${file.name}]")

        if (file.contentType != "text/csv") throw BadRequestException("No valid CSV file")

        repository.deleteAllInBatch()

        var fees = file.bytes.inputStream().bufferedReader()
            .readLines()
            .drop(1) // skip first line
            .toSet() // ignore duplicates
            .map { line -> validator.validate(line) }

        repository.saveAll(fees)

        logger.info("Insurance fees uploaded [size = ${fees.size}]")
    }

    @GetMapping
    fun getAll(): List<InsuranceFeeResponse> {
        return repository.findAll().map { it.toApi() }
    }

    @GetMapping("/by")
    fun getByCategoryAndValue(
        @RequestParam("category") category: String,
        @RequestParam("value") value: Long
    ): InsuranceFeeResponse {
        return repository.findOneByCategoryAndValue(category, value)?.toApi() ?: throw NotFoundException("Fee does not exits")
    }
}