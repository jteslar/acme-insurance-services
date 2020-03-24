package com.acme.services.fees

import com.acme.services.fees.validation.InsuranceFeeValidator
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@RunWith(SpringRunner::class)
@WebMvcTest
@ActiveProfiles("test")
class InsuranceFeeControllerTest {

    @TestConfiguration
    internal class InsuranceFeeValidatorTestContextConfiguration {

        @Bean
        fun insuranceFeeValidator(): InsuranceFeeValidator {
            return InsuranceFeeValidator()
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var repository: InsuranceFeeRepository

    @InjectMocks
    private lateinit var controller: InsuranceFeeController

    private val emptyFees = "Category;Lim_low;Lim_Top;Fee"
    private val wrongFees = "Category;Lim_low;Lim_Top;Fee\ncategory;0;10;0,5\ncategory;ss;10;0,5"
    private val correctFees = "Category;Lim_low;Lim_Top;Fee\ncategory;0;10;0,5"

    private fun newFile(content: String): MockMultipartFile = MockMultipartFile("file", "fees.csv", "text/csv", content.toByteArray())

    @Test
    fun `upload an empty file is ok`() {

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/fees/upload")
                .file(newFile(emptyFees))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        Mockito.verify(repository).saveAll(emptyList())
    }

    @Test
    fun `upload with invalid data fails`() {

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/fees/upload")
                .file(newFile(wrongFees))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

    @Test
    fun `upload a correct file is ok`() {

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/fees/upload")
                .file(newFile(correctFees))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        Mockito.verify(repository).saveAll(
            listOf(
                InsuranceFee(
                    productCategory = "category",
                    limitLow = 0,
                    limitTop = 10,
                    feePercentage = 0.5
                )
            )
        )
    }
}