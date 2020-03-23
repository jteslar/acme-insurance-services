package com.acme.services.users.model

import com.acme.services.users.User
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class UserRequest {

    @NotBlank
    @Size(min = 3, max = 15)
    var username: String? = null

    @NotBlank
    @Size(min = 3, max = 15)
    var password: String? = null

    override fun toString(): String {
        return "UserRequest(username=$username, password=$password)"
    }
}

fun UserRequest.toDomain(): User = User(username = username!!, password = password!!)