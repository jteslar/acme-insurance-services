package com.acme.services.contracts.client

import com.acme.services.commons.exceptions.BadRequestException
import com.acme.services.products.model.ProductResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ProductClient {

    @Value("\${app.client.get-products-url}")
    private lateinit var productsUrl: String

    @Value("\${app.client.get-product-url}")
    private lateinit var productUrl: String

    private var restTemplate: RestTemplate = RestTemplate()

    fun getAll(): Map<String, ProductResponse> =
        restTemplate.getForObject(productsUrl, Array<ProductResponse>::class.java)
            .associateBy { product -> product.name }

    fun getByName(name: String): ProductResponse =
        restTemplate.getForObject("$productUrl?name={name}", ProductResponse::class.java, name)
            ?: throw BadRequestException("Product $name does not exist")

}