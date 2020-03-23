package com.acme.services.contracts.client

import com.acme.services.commons.exceptions.BadRequestException
import com.acme.services.fees.model.InsuranceFeeResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class InsuranceFeeClient {

    @Value("\${app.client.get-fees-url}")
    private lateinit var feesUrl: String

    @Value("\${app.client.get-fee-url}")
    private lateinit var feeUrl: String

    private var restTemplate: RestTemplate = RestTemplate()

    fun getAll(): List<InsuranceFeeResponse> =
        restTemplate.getForObject(feesUrl, Array<InsuranceFeeResponse>::class.java).toList()

    fun findByCategoryAndValue(category: String, value: Long): InsuranceFeeResponse =
        restTemplate.getForObject("$feeUrl?category={category}&value={value}", InsuranceFeeResponse::class.java, category, value)
            ?: throw BadRequestException("Insurance fee for $category not found")
}