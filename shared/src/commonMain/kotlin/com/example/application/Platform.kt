package com.example.application

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform