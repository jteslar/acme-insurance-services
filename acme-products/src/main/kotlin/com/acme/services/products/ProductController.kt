package com.acme.services.products

import com.acme.services.commons.exceptions.BadRequestException
import com.acme.services.commons.exceptions.NotFoundException
import com.acme.services.products.model.ProductResponse
import com.acme.services.products.validation.ProductValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.transaction.Transactional

@RestController
@RequestMapping("/products")
class ProductController {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ProductController::class.java)
    }

    @Autowired
    private lateinit var repository: ProductRepository

    @Autowired
    private lateinit var validator: ProductValidator

    @Transactional
    @PostMapping("/upload")
    fun upload(@RequestPart("file") file: MultipartFile) {
        logger.info("Uploading products [file = ${file.name}]")

        if (file.contentType != "text/csv") throw BadRequestException("No valid CSV file")

        repository.deleteAllInBatch()

        var products = file.bytes.inputStream().bufferedReader()
            .readLines()
            .drop(1) // skip first line
            .toSet() // ignore duplicates
            .map { line -> validator.validate(line) }

        repository.saveAll(products)

        logger.info("Products uploaded [size = ${products.size}]")
    }

    @GetMapping
    fun getAll(): List<ProductResponse> {
        return repository.findAll().map { it.toApi() }
    }

    @GetMapping("/by")
    fun getByName(@RequestParam("name") name: String): ProductResponse {
        return repository.findOneByName(name)?.toApi() ?: throw NotFoundException("Product does not exits")
    }
}