package com.example.nextstep.data.model

data class TeacherConversationDto(
    val applicationId: String,
    val studentProfileId: String = "",
    val studentName: String,
    val studentEmail: String? = null,
    val offerTitle: String? = null,
    val companyName: String? = null,
    val lastMessage: String? = null,
    val lastMessageAt: String? = null,
    val unreadCount: Int = 0
)