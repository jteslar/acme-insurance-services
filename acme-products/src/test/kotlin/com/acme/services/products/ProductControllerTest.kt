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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@RunWith(SpringRunner::class)
@WebMvcTest
@ActiveProfiles("test")
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

    private val emptyProducts = "Product Name;Category"
    private val wrongProducts = "Product Name;Category\nproduct"
    private val correctProducts = "Product Name;Category\nproduct;category"

    private fun newFile(content: String): MockMultipartFile = MockMultipartFile("file", "fees.csv", "text/csv", content.toByteArray())
    private fun newPdfFile(content: String): MockMultipartFile = MockMultipartFile("file", "fees.csv", "text/pdf", content.toByteArray())

    @Test
    fun `upload with wron ContentType fails`() {

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/products/upload")
                .file(newPdfFile(emptyProducts))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

    @Test
    fun `upload an empty file is ok`() {

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/products/upload")
                .file(newFile(emptyProducts))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        Mockito.verify(repository).saveAll(emptyList())
    }

    @Test
    fun `upload with invalid data fails`() {

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/products/upload")
                .file(newFile(wrongProducts))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

    @Test
    fun `upload a correct file is ok`() {

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/products/upload")
                .file(newFile(correctProducts))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        Mockito.verify(repository).saveAll(listOf(Product(name = "product", category = "category")))
    }
}