package com.acme.services.products.validation

import com.acme.services.commons.exceptions.BadRequestException
import com.acme.services.products.Product
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductValidator {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ProductValidator::class.java)
    }

    fun validate(line: String): Product {
        try {
            var productProperties = line.split(";")
            return Product(
                name = productProperties[0],
                category = if (productProperties[1].isNullOrBlank()) "Generic" else productProperties[1]
            )
        } catch (e: Exception) {
            logger.error("Error parsing product [$line]", e)
            throw BadRequestException("Error parsing product [$line]")
        }
    }
}