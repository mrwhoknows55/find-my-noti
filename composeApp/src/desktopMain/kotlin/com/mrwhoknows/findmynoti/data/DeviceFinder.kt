package com.mrwhoknows.findmynoti.data

import androidx.compose.runtime.Stable
import com.mrwhoknows.findmynoti.server.HostDevice
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URI

data class Address(
    val address: String,
    val subnetMask: Int
)



class DeviceFinder {
    private val client = HttpClient {
        engine {
            pipelining = true
        }
    }
    private val _reachableHosts = MutableStateFlow(listOf<HostDevice>())
    val reachableHosts = _reachableHosts.asStateFlow()

    private var job: Job? = null

    fun findDevices() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            allPossibleHosts.collect { urls ->
                urls.forEach { url ->
                    launch {
                        checkReachability("http://$url:1337/ping")
                    }
                }
                delay(150)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    private val allPossibleHosts = flow {
        val addressList = mutableListOf<Address>()
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()

            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                if (!networkInterface.isLoopback && networkInterface.isUp) {
                    addressList.addAll(networkInterface.interfaceAddresses.filter { it.broadcast != null }
                        .map {
                            val subnetMask = it.networkPrefixLength.toString()
                            Address(it.address.hostAddress, subnetMask.toInt())
                        })
                }
            }
            addressList.flatMap { (ipAddress, subnetPrefix) ->
                val numbers = ipAddress.split(".", limit = 4).map(String::toInt)
                val index = subnetPrefix.div(8)
                val allIps = (index..3).flatMap { current ->
                    val firstPart = numbers.take(current).joinToString(separator = ".")
                    val lastPart = numbers.takeLast(3 - current).joinToString(separator = ".")

                    val ips = (0..255).map { currentNum ->
                        val address = "$firstPart.$currentNum".let { halfPart ->
                            if (lastPart.isNotBlank()) {
                                "$halfPart.$lastPart"
                            } else {
                                halfPart
                            }
                        }
                        address
                    }
                    ips.distinct().windowed(5, 5).map {
                        emit(it)
                    }
                }
                allIps.distinct()
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun checkReachability(url: String) = withContext(Dispatchers.IO) {
        runCatching {
            val response: HttpResponse = client.get(url)
            if (response.call.response.status.isSuccess()) {
                _reachableHosts.update {
                    (it + HostDevice(
                        address = url,
                        deviceName = response.body<String>()
                    )).filter { it.address.isNotBlank() }
                }
            }
        }.getOrElse {
            if (it is CancellationException) {
                println(it)
                throw it
            }
        }
    }

}