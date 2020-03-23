package com.acme.services.contracts

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "insurance_contracts")
data class InsuranceContract (

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "delivery_date", nullable = false)
    var deliveryDate: LocalDate = LocalDate.now(),

    @Column(name = "product_name", nullable = false)
    var productName: String = "",

    @Column(name = "product_category", nullable = false)
    var productCategory: String = "",

    @Column(name = "declared_value", nullable = false)
    var declaredValue: Long = 0,

    @Column(name = "fee_percentage", nullable = false)
    var feePercentage: Double = 0.0,

    @Column(name = "fee_amount", nullable = false)
    var feeAmount: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0
)