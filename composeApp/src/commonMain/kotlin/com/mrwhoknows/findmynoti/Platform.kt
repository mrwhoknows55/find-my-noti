package com.mrwhoknows.findmynoti

interface Platform {
    val name: String
    val deviceName: String
}

expect fun getPlatform(): Platform
