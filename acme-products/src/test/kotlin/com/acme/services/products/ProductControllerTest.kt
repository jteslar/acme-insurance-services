package com.acme.services.products

import com.acme.services.products.validation.ProductValidator
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
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@RunWith(SpringRunner::class)
@WebMvcTest
class ProductControllerTest {

    @TestConfiguration
    internal class ProductValidatorTestContextConfiguration {

        @Bean
        fun productValidator(): ProductValidator {
            return ProductValidator()
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var repository: ProductRepository

    @InjectMocks
    private lateinit var controller: ProductController

    private val emptyProducts = "product;category"
    private val wrongProducts = "product;category\nproduct"
    private val correctProducts = "product;category\nproduct;category"

    @Test
    fun `upload with wron CgontentType fails`() {
        val file = MockMultipartFile("file", "products.csv", "text/pdf", emptyProducts.toByteArray())

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/products/upload")
                .file(file)
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

    @Test
    fun `upload an empty file is ok`() {
        val file = MockMultipartFile("file", "products.csv", "text/csv", emptyProducts.toByteArray())

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/products/upload")
                .file(file)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        Mockito.verify(repository).saveAll(emptyList())
    }

    @Test
    fun `upload with invalid data fails`() {
        val file = MockMultipartFile("file", "products.csv", "text/csv", wrongProducts.toByteArray())

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/products/upload")
                .file(file)
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

    @Test
    fun `upload a correct file is ok`() {
        val file =
            MockMultipartFile("file", "products.csv", "text/csv", correctProducts.toByteArray())

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/products/upload")
                .file(file)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        Mockito.verify(repository).saveAll(listOf(Product(name = "product", category = "category")))
    }
}