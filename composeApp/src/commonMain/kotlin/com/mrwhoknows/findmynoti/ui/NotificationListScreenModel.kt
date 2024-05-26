package com.mrwhoknows.findmynoti.ui

import cafe.adriel.voyager.core.model.ScreenModel
import com.mrwhoknows.findmynoti.data.repo.NotificationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
class NotificationListScreenModel(
    private val repository: NotificationRepository
) : ScreenModel {
    private val searchKeyword = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifications = searchKeyword.debounce(300).flatMapLatest { keyword ->
        if (keyword.isBlank()) {
            repository.getNotifications(limit = 20).flow
        } else {
            repository.searchNotifications(keyword = keyword, limit = 20).flow
        }
    }

    fun search(keyword: String) {
        searchKeyword.update { keyword }
    }

    private fun getNotifications() {
        searchKeyword.update { "" }
    }

    init {
        getNotifications()
    }
}