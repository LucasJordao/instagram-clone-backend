package com.lucas.instagram.clone.entrypoint.dto

data class UserRegisterRequest (
    val username: String,
    val name: String? = null,
    val email: String? = null,
    val perfilImage: String? = null,
    val password: String
)