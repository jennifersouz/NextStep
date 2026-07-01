package com.example.nextstep.ui.screens.company

import com.example.nextstep.data.model.CompanyApplicationDto

enum class ApplicationStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    WAITING_STUDENT,
    WITH_ADVISOR,
    UNKNOWN
}

fun normalizeApplicationStatus(status: String?): ApplicationStatus {
    return when (status?.trim()?.lowercase()) {
        "pending", "pendente" -> ApplicationStatus.PENDING
        "accepted", "aceite", "aceita" -> ApplicationStatus.ACCEPTED
        "rejected", "rejeitada", "recusada" -> ApplicationStatus.REJECTED
        "waiting_student", "waiting_for_student", "aguardar_aluno", "a_aguardar_aluno" ->
            ApplicationStatus.WAITING_STUDENT
        "with_advisor", "com_orientador" ->
            ApplicationStatus.WITH_ADVISOR
        else -> ApplicationStatus.UNKNOWN
    }
}

fun determineApplicationStatus(application: CompanyApplicationDto): ApplicationStatus {
    val rawStatus = application.status.trim().lowercase()

    return when {
        rawStatus in listOf("accepted", "aceite", "aceita") &&
            !application.studentPresenceConfirmed ->
            ApplicationStatus.WAITING_STUDENT

        !application.advisorProfileId.isNullOrBlank() ->
            ApplicationStatus.WITH_ADVISOR

        else -> normalizeApplicationStatus(application.status)
    }
}
