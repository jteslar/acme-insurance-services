package com.acme.services.products

import com.acme.services.products.model.ProductResponse
import javax.persistence.*

@Entity
@Table(name = "products")
data class Product (

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "name", nullable = false, unique = true)
    var name: String = "",

    @Column(name = "category", nullable = false)
    var category: String = ""
)

fun Product.toApi(): ProductResponse =
    ProductResponse(name = name, category = category)