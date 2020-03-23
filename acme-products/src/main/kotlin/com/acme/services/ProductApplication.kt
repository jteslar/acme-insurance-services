package com.acme.services

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ProductApplication>(*args)
        }
    }
}
