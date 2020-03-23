package com.acme.services.users

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import javax.persistence.*

@Entity
@Table(name = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class User (

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "username", nullable = false, unique = true)
    var username: String = "",

    @Column(name = "password")
    var password: String? = null
)