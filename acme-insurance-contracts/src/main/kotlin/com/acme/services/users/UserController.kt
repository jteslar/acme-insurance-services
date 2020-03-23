package com.acme.services.users

import com.acme.services.commons.exceptions.ConflictException
import com.acme.services.users.model.UserRequest
import com.acme.services.users.model.toDomain
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import javax.transaction.Transactional

@RestController
@RequestMapping("/users")
class UserController {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)
    }

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var repository: UserRepository

    @Transactional
    @PostMapping("/register")
    fun register(@Validated @RequestBody user: UserRequest) {
        logger.info("Registering user [username = ${user.username}]")

        repository.findByUsername(user.username!!)
            ?.let { throw ConflictException("User already exists") }
            ?: repository.save(user.toDomain().apply { password = passwordEncoder.encode(password) })

        logger.info("User registered [username = ${user.username}]")
    }

    @GetMapping("/me")
    fun getLoggedUser(@ApiIgnore auth: Authentication): User {
        return repository.findByIdOrNull(auth.name.toLong())?.apply { password = null }
            ?: throw ConflictException("Logged in user not found")
    }
}