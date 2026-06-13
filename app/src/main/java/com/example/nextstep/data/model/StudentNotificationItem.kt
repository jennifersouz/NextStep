package com.example.nextstep.data.model

sealed class StudentNotificationItem {
    abstract val id: String
    abstract val type: String
    abstract val isUnread: Boolean
    abstract val sortDate: String
    abstract val applicationId: String?

    data class ViewBased(
        val notification: StudentNotificationDto
    ) : StudentNotificationItem() {
        override val id = notification.id
        override val type = notification.type
        override val isUnread = notification.isUnread
        override val sortDate = notification.sortDate
        override val applicationId = notification.id
    }

    data class TableBased(
        val notification: NotificationDto
    ) : StudentNotificationItem() {
        override val id = notification.id
        override val type = notification.type
        override val isUnread = !notification.isRead
        override val sortDate = notification.createdAt.orEmpty()
        override val applicationId = notification.applicationId
    }
}
