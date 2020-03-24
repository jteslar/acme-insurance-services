package com.acme.services.config

import com.acme.services.users.auth.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var userService: UserService

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): AuthenticationProvider = DaoAuthenticationProvider().also {
        it.setPasswordEncoder(passwordEncoder())
        it.setUserDetailsService(userService)
    }

    override fun userDetailsService(): UserDetailsService {
        return userService
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider())
        auth.userDetailsService(userService)
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
            .antMatchers("/contracts/**").authenticated()
            .antMatchers("/fees/**").authenticated()
            .antMatchers("/products/**").authenticated()
            .antMatchers("/users/me").authenticated()
            .antMatchers("/users/register").permitAll()
            .anyRequest().permitAll()
            .and().httpBasic()

    }

}