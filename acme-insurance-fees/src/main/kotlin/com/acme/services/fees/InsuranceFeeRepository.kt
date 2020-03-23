package com.acme.services.fees

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface InsuranceFeeRepository : JpaRepository<InsuranceFee, Long> {

    @Query("select fee from InsuranceFee fee where fee.productCategory = :category and " +
            "fee.limitLow <= :value and (fee.limitTop is null or fee.limitTop >= :value)")
    fun findOneByCategoryAndValue(category: String, value: Long): InsuranceFee?
}