package com.acme.services.contracts

import com.acme.services.contracts.client.InsuranceFeeClient
import com.acme.services.contracts.client.ProductClient
import com.acme.services.contracts.validation.InsuranceContractValidator
import com.acme.services.fees.model.InsuranceFeeResponse
import com.acme.services.products.model.ProductResponse
import com.acme.services.users.UserRepository
import com.acme.services.users.auth.UserService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate


@RunWith(SpringRunner::class)
@WebMvcTest
@ActiveProfiles("test")
class InsuranceContractControllerTest {

    @TestConfiguration
    internal class InsuranceContractValidatorConfiguration {

        @Bean
        fun insuranceContractValidator(): InsuranceContractValidator {
            return InsuranceContractValidator()
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var repository: InsuranceContractRepository

    @MockBean
    private lateinit var productClient: ProductClient

    @MockBean
    private lateinit var insuranceFeeClient: InsuranceFeeClient

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var controller: InsuranceContractController

    private val contracts = "Delivery Date;Product;Declared Value\n01.01.19;product;1.000,00"
    private val product = ProductResponse(name = "product", category = "category")
    private val fee1 =
        InsuranceFeeResponse(productCategory = "category", limitLow = 0, limitTop = 999, feePercentage = 10.0)
    private val fee2 =
        InsuranceFeeResponse(productCategory = "category", limitLow = 1000, limitTop = 1999, feePercentage = 20.0)
    private val contract1 = InsuranceContract(
        deliveryDate = LocalDate.parse("2019-01-01"),
        productName = "product",
        productCategory = "category",
        declaredValue = 1000,
        feePercentage = 20.0,
        feeAmount = 200,
        userId = 1
    )
    private val contract2 = InsuranceContract(
        deliveryDate = LocalDate.parse("2019-01-01"),
        productName = "product",
        productCategory = "category",
        declaredValue = 500,
        feePercentage = 10.0,
        feeAmount = 50,
        userId = 1
    )

    private fun newFile(content: String): MockMultipartFile =
        MockMultipartFile("file", "contracts.csv", "text/csv", content.toByteArray())

    @WithMockUser("1")
    @Test
    fun `upload a correct file an calculate fee`() {

        Mockito.`when`(productClient.getAll()).thenReturn(mapOf("product" to product))
        Mockito.`when`(insuranceFeeClient.getAll()).thenReturn(listOf(fee1, fee2))

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/contracts/upload")
                .file(newFile(contracts))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        Mockito.verify(repository).saveAll(listOf(contract1))
    }

    @WithMockUser("1")
    @Test
    fun `create new contract and calculate fee`() {

        Mockito.`when`(productClient.getByName("product")).thenReturn(product)
        Mockito.`when`(insuranceFeeClient.getByCategoryAndValue("category", 500)).thenReturn(fee1)
        Mockito.`when`(repository.save(contract2)).thenReturn(contract2)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/contracts")
                .content("{\"deliveryDate\":\"2019-01-01\",\"productName\":\"product\",\"declaredValue\":500}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated)

        Mockito.verify(repository).save(contract2)
    }
}