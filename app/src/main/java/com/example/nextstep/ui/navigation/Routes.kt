package com.example.nextstep.ui.navigation

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
    const val ADVISOR_DASHBOARD = "advisor_dashboard"

    fun chat(applicationId: String): String {
        return "chat/$applicationId"
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
}