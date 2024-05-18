package com.mrwhoknows.findmynoti.ui.main

import com.mrwhoknows.findmynoti.data.DeviceFinder
import com.mrwhoknows.findmynoti.server.HostDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val deviceFinder: DeviceFinder
) {

    private val _state = MutableStateFlow(DeviceFinderState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null
    fun startFinding() {
        searchJob?.cancel()
        _state.update { it.copy(isLoading = true, error = "") }
        deviceFinder.findDevices()
        searchJob = CoroutineScope(Dispatchers.IO).launch {
            deviceFinder.reachableHosts.collectLatest { reachableHosts ->
                println("collect reachable ${reachableHosts.size} $reachableHosts")
                // TODO handle timeout
                _state.update {
                    it.copy(
                        reachableDevices = reachableHosts.distinctBy { it.address.isNotBlank() && it.deviceName.isNotBlank() },
                        error = ""
                    )
                }
            }
        }
    }

    fun stopFinding() {
        _state.update { it.copy(isLoading = false, error = "") }
        searchJob?.cancel()
        deviceFinder.stop()
    }

}

data class DeviceFinderState(
    val isLoading: Boolean = false,
    val reachableDevices: List<HostDevice> = emptyList(),
    val error: String = ""
) {
    val startStopButtonText: String
        get() = if (isLoading) "Stop" else "Start"
}