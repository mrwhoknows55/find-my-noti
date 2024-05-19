package com.mrwhoknows.findmynoti.util

import com.mrwhoknows.findmynoti.util.Platform
import java.net.InetAddress

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val deviceName: String = InetAddress.getLocalHost().hostName.ifBlank { "Desktop" }
}

actual fun getPlatform(): Platform = JVMPlatform()
