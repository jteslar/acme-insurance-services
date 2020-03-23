package com.acme.services.contracts.model

import com.acme.services.contracts.InsuranceContract
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class InsuranceContractRequest {

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    var deliveryDate: LocalDate? = null

    @NotBlank
    var productName: String? = null

    @NotNull
    @Min(0)
    var declaredValue: Long? = null

    override fun toString(): String {
        return "InsuranceContractRequest(deliveryDate=$deliveryDate, productName=$productName, declaredValue=$declaredValue)"
    }
}

fun InsuranceContractRequest.toDomain(): InsuranceContract =
    InsuranceContract(deliveryDate = deliveryDate!!, productName = productName!!, declaredValue = declaredValue!!)