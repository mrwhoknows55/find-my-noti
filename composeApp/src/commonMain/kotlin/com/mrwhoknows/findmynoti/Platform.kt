package com.mrwhoknows.findmynoti

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
