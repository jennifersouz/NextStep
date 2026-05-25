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

    fun studentApplication(offerId: String): String {
        return "$STUDENT_APPLICATION/$offerId"
    }

    fun studentOfferDetail(offerId: String): String {
        return "$STUDENT_OFFER_DETAIL/$offerId"
    }
}