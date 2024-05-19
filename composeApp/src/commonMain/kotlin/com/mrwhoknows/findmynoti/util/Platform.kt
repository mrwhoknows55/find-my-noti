package com.mrwhoknows.findmynoti.util

interface Platform {
    val name: String
    val deviceName: String
}

expect fun getPlatform(): Platform
