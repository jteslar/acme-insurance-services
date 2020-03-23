package com.acme.services.fees

import com.acme.services.fees.model.InsuranceFeeResponse
import javax.persistence.*

@Entity
@Table(
    name = "insurance_fees",
    uniqueConstraints= [UniqueConstraint(columnNames = arrayOf("limit_low", "limit_top", "product_category"))]
)
data class InsuranceFee(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "limit_low", nullable = false)
    var limitLow: Long = 0,

    @Column(name = "limit_top")
    var limitTop: Long? = null,

    @Column(name = "fee_percentage", nullable = false)
    var feePercentage: Double = 0.0,

    @Column(name = "product_category", nullable = false)
    var productCategory: String = ""
)

fun InsuranceFee.toApi(): InsuranceFeeResponse =
    InsuranceFeeResponse(
        limitLow = limitLow,
        limitTop = limitTop,
        feePercentage = feePercentage,
        productCategory = productCategory
    )