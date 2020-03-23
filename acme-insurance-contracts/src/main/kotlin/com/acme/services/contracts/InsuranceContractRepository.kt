package com.acme.services.contracts

import org.springframework.data.jpa.repository.JpaRepository

interface InsuranceContractRepository : JpaRepository<InsuranceContract, Long> {

    fun findAllByUserId(userId: Long): List<InsuranceContract>

    fun findAllByUserIdAndProductNameContaining(userId: Long, productName: String): List<InsuranceContract>

    fun deleteByUserId(userId: Long)
}