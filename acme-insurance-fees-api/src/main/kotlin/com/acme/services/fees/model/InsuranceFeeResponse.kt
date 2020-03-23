package com.acme.services.fees.model

data class InsuranceFeeResponse(var limitLow: Long = 0, var limitTop: Long? = null, var feePercentage: Double = 0.0, var productCategory: String = "")