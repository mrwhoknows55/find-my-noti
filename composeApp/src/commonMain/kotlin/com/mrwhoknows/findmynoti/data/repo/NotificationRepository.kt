package com.mrwhoknows.findmynoti.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.paging.PagingSourceLoadResultPage
import com.mrwhoknows.findmynoti.ui.model.Notification
import kotlinx.coroutines.CancellationException

class NotificationRepository(
    private val dataSource: NotificationDataSource
) {

    fun getNotificationsByPackageName(packageName: String, limit: Int = 20) = getPager(
        NotificationListType.ByPackageNamePaginated(packageName, limit)
    )

    fun searchNotifications(keyword: String, limit: Int = 20) = getPager(
        NotificationListType.SearchPaginated(keyword, limit)
    )

    fun getNotifications(limit: Int = 20) = getPager(
        NotificationListType.ByTimestampDscPaginated(limit)
    )

    private fun getPager(type: NotificationListType): Pager<Int, Notification> {
        val pagingConfig = PagingConfig(
            pageSize = type.limit,
            initialLoadSize = type.limit
        )
        return Pager(pagingConfig) {
            NotificationPagingSource(dataSource, type)
        }
    }

}

sealed class NotificationListType(
    open val limit: Int,
) {
    data class ByTimestampDscPaginated(
        override val limit: Int,
    ) : NotificationListType(limit)

    data class SearchPaginated(
        val keyword: String,
        override val limit: Int,
    ) : NotificationListType(limit)

    data class ByPackageNamePaginated(
        val packageName: String,
        override val limit: Int,
    ) : NotificationListType(limit)
}

private class NotificationPagingSource(
    private val repository: NotificationDataSource,
    private val type: NotificationListType
) : PagingSource<Int, Notification>() {

    private val limit: Int = type.limit

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        val pageNo = params.key ?: 1
        val offset = pageNo.minus(1).times(limit).coerceAtLeast(0)

        val result = runCatching {
            when (type) {
                is NotificationListType.ByTimestampDscPaginated -> {
                    repository.getNotificationByOffsetAndLimit(
                        limit = limit, offset = offset
                    )
                }

                is NotificationListType.ByPackageNamePaginated -> {
                    repository.getNotificationByPackageName(
                        packageName = type.packageName, limit = limit, offset = offset
                    )
                }

                is NotificationListType.SearchPaginated -> {
                    repository.searchNotifications(
                        keyword = type.keyword, limit = limit, offset = offset
                    )
                }
            }

        }.getOrElse {
            if (it is CancellationException) throw it
            emptyList()
        }

        return PagingSourceLoadResultPage(
            data = result,
            prevKey = pageNo.takeIf { it > 1 },
            nextKey = if (result.count() == limit) pageNo + 1 else null,
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? = null

}