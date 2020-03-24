package com.acme.services.contracts

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate


@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
class InsuranceContractRepositoryTest {

    @Autowired
    private lateinit var repository: InsuranceContractRepository

    @Test
    fun `create and find insurance contract by user and product`() {

        val contract = InsuranceContract(
            deliveryDate = LocalDate.parse("2019-01-01"),
            productName = "product",
            productCategory = "category",
            declaredValue = 1000,
            feePercentage = 20.0,
            feeAmount = 200,
            userId = 10
        )
        repository.save(contract)

        Assert.assertEquals(repository.findAllByUserIdAndProductNameContaining(10, "product"), listOf(contract))
    }
}