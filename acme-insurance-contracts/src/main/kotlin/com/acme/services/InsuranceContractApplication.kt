package com.acme.services

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InsuranceContractApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<InsuranceContractApplication>(*args)
        }
    }
}
