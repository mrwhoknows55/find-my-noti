package com.mrwhoknows.findmynoti.util

import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import kotlin.random.Random


val currentPrivateIPAddress: String
    get() {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (networkInterface in interfaces) {
            if (!networkInterface.isUp || networkInterface.isLoopback) continue

            for (inetAddress in networkInterface.inetAddresses) {
                if (!inetAddress.isLoopbackAddress && inetAddress is InetAddress && inetAddress.address.size == 4) {
                    return inetAddress.hostAddress
                }
            }
        }
        return ""
    }

tailrec fun getUnusedRandomPortNumber(): Int {
    val port = Random.nextInt(49152, 60000)
    if (port.isPortUnused()) {
        return port
    }
    return getUnusedRandomPortNumber()
}

fun Int.isPortUnused(): Boolean = runCatching {
    println("isPortUnused: $this")
    val serverSocket = ServerSocket(this)
    serverSocket.close()
    true
}.getOrElse { false }