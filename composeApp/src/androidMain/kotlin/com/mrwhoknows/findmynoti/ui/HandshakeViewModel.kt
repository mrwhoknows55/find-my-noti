package com.mrwhoknows.findmynoti.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrwhoknows.findmynoti.util.Constants.NotificationServerUrl
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HandshakeViewModel : ViewModel() {

    fun connectWithDesktop(desktopUrl: String, notificationServerUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = URLBuilder(desktopUrl).apply {
                parameters.append(NotificationServerUrl.name, notificationServerUrl)
            }.build()
            Napier.i("connectWithDesktop: $url")
            val result = HttpClient().use {
                it.get(url)
            }
            Napier.i(result.bodyAsText())
        }
    }
}