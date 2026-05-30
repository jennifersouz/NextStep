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