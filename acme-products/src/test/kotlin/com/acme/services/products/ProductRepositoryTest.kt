package com.acme.services.products

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private lateinit var repository: ProductRepository

    @Test
    fun `create and find product by name`() {

        val product = Product(name = "product", category = "category")
        repository.save(product)

        Assert.assertEquals(repository.findOneByName("product"), product)
    }
}