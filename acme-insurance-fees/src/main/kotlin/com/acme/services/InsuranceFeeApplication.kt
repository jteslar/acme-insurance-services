package com.acme.services

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InsuranceFeeApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<InsuranceFeeApplication>(*args)
        }
    }
}
