package com.example.nextstep.ui.navigation

import android.net.Uri

object Routes {
    const val SPLASH = "splash"
    const val INTRO = "intro"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val STUDENT_DASHBOARD = "student_dashboard"
    const val COMPANY_DASHBOARD = "company_dashboard"
    const val STUDENT_OFFER_DETAIL = "student_offer_detail"
    const val STUDENT_OFFER_DETAIL_ARG = "offerId"

    const val STUDENT_APPLICATION = "student_application"
    const val STUDENT_APPLICATION_ARG = "offerId"
    const val STUDENT_SUBMITTED_APPLICATIONS = "student_submitted_applications"
    const val STUDENT_SUBMITTED_APPLICATION_DETAIL = "student_submitted_application_detail"
    const val STUDENT_SUBMITTED_APPLICATION_DETAIL_ARG = "applicationId"
    const val COMPANY_OFFER_DETAIL = "company_offer_detail/{offerId}"
    const val COMPANY_EDIT_OFFER = "company_edit_offer/{offerId}"
    const val CHAT = "chat/{applicationId}"
    const val CHAT_ARG = "applicationId"
    const val APPLICATION_CHAT = "application_chat/{applicationId}?name={name}"
    const val APPLICATION_CHAT_ARG = "applicationId"
    const val APPLICATION_CHAT_NAME_ARG = "name"
    const val ADVISOR_DASHBOARD = "advisor_dashboard"
    const val INSTITUTION_DASHBOARD = "institution_dashboard"
    const val INSTITUTION_USERS = "institution_users"
    const val ADD_INSTITUTION_USER = "add_institution_user"
    const val TEACHER_DASHBOARD = "teacher_dashboard"
    const val COMPANY_STUDENT_PROFILE = "company_student_profile/{applicationId}"
    const val COMPANY_STUDENT_PROFILE_ARG = "applicationId"
    const val ADVISOR_EDIT_PROFILE = "advisor_edit_profile"
    const val ADVISOR_STUDENT_DETAIL = "advisor_student_detail/{applicationId}"
    const val ADVISOR_STUDENT_DETAIL_ARG = "applicationId"
    const val ADVISOR_EVALUATE_STUDENT = "advisor_evaluate_student/{applicationId}"
    const val ADVISOR_EVALUATE_STUDENT_ARG = "applicationId"
    const val ADVISOR_NOTIFICATIONS = "advisor_notifications"

    const val COMPANY_APPLICATION_DETAIL = "company_application_detail/{applicationId}"
    const val COMPANY_APPLICATION_DETAIL_ARG = "applicationId"

    const val COMPANY_ASSIGN_ADVISOR = "company_assign_advisor/{applicationId}"
    const val COMPANY_ASSIGN_ADVISOR_ARG = "applicationId"

    fun companyApplicationDetail(applicationId: String): String {
        return "company_application_detail/$applicationId"
    }

    fun companyAssignAdvisor(applicationId: String): String {
        return "company_assign_advisor/$applicationId"
    }

    fun companyStudentProfile(applicationId: String): String {
        return "company_student_profile/$applicationId"
    }

    fun chat(applicationId: String): String {
        return "chat/$applicationId"
    }

    fun applicationChat(applicationId: String, name: String? = null): String {
        return if (!name.isNullOrBlank()) {
            val encodedName = Uri.encode(name)
            "application_chat/$applicationId?name=$encodedName"
        } else {
            "application_chat/$applicationId"
        }
    }
    fun companyOfferDetail(offerId: String): String {
        return "company_offer_detail/$offerId"
    }

    fun companyEditOffer(offerId: String): String {
        return "company_edit_offer/$offerId"
    }
    fun studentSubmittedApplicationDetail(applicationId: String): String {
        return "$STUDENT_SUBMITTED_APPLICATION_DETAIL/$applicationId"
    }
    fun studentApplication(offerId: String): String {
        return "$STUDENT_APPLICATION/$offerId"
    }

    fun studentOfferDetail(offerId: String): String {
        return "$STUDENT_OFFER_DETAIL/$offerId"
    }

    fun advisorStudentDetail(applicationId: String): String {
        return "advisor_student_detail/$applicationId"
    }

    fun advisorEvaluateStudent(applicationId: String): String {
        return "advisor_evaluate_student/$applicationId"
    }
}
