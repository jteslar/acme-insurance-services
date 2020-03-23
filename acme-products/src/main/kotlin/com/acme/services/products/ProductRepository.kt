package com.acme.services.products

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {

    fun findOneByName(name: String): Product?
}