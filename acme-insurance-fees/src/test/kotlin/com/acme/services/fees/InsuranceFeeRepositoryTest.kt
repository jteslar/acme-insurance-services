package com.acme.services.fees

import com.acme.services.fees.InsuranceFee
import com.acme.services.fees.InsuranceFeeRepository
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
class InsuranceFeeRepositoryTest {

    @Autowired
    private lateinit var repository: InsuranceFeeRepository

    @Test
    fun `create and find insurance fee by category and value`() {

        val fee = InsuranceFee(productCategory = "category", limitLow = 0, limitTop = 10, feePercentage = 0.5)
        repository.save(fee)

        Assert.assertEquals(repository.findOneByCategoryAndValue("category", 5), fee)
    }
}