package com.example.nextstep.data.model

data class AdvisorTaskListItemDto(
    val id: String,
    val applicationId: String,
    val title: String,
    val description: String? = null,
    val studentName: String,
    val offerTitle: String? = null,
    val companyName: String? = null,
    val status: String,
    val dueDate: String? = null,
    val completedAt: String? = null
)

data class AdvisorConversationDto(
    val applicationId: String,
    val studentName: String,
    val studentEmail: String? = null,
    val offerTitle: String? = null,
    val lastMessage: String? = null,
    val lastMessageAt: String? = null,
    val unreadCount: Int = 0
)