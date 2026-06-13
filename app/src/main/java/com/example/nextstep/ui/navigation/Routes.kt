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

    const val STUDENT_INTERNSHIP_DETAIL = "student_internship_detail"
    const val STUDENT_INTERNSHIP_DETAIL_ARG = "internshipId"
    const val STUDENT_SEARCH_ADVISOR = "student_search_advisor/{internshipId}"
    const val STUDENT_SEARCH_ADVISOR_ARG = "internshipId"

    const val COMPANY_PROFILE = "company_profile/{companyProfileId}"
    const val COMPANY_PROFILE_ARG = "companyProfileId"

    const val COMPANY_OFFER_DETAIL = "company_offer_detail/{offerId}"
    const val COMPANY_EDIT_OFFER = "company_edit_offer/{offerId}"
    const val CHAT = "chat/{applicationId}"
    const val CHAT_ARG = "applicationId"

    const val APPLICATION_CHAT = "application_chat/{applicationId}?name={name}&offerTitle={offerTitle}&studentProfileId={studentProfileId}"
    const val APPLICATION_CHAT_ARG = "applicationId"
    const val APPLICATION_CHAT_NAME_ARG = "name"
    const val APPLICATION_CHAT_OFFER_ARG = "offerTitle"
    const val APPLICATION_CHAT_STUDENT_ID_ARG = "studentProfileId"

    const val ADVISOR_DASHBOARD = "advisor_dashboard"
    const val INSTITUTION_DASHBOARD = "institution_dashboard"
    const val INSTITUTION_USERS = "institution_users"
    const val ADD_INSTITUTION_USER = "add_institution_user"
    const val INSTITUTION_TEACHERS = "institution_teachers"
    const val INSTITUTION_TEACHER_DETAIL = "institution_teacher_detail/{teacherProfileId}"
    const val INSTITUTION_TEACHER_DETAIL_ARG = "teacherProfileId"
    const val INSTITUTION_STUDENTS = "institution_students"
    const val INSTITUTION_STUDENT_DETAIL = "institution_student_detail/{studentProfileId}"
    const val INSTITUTION_STUDENT_DETAIL_ARG = "studentProfileId"

    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_CREATE_USER = "admin_create_user"
    const val TEACHER_DASHBOARD = "teacher_dashboard"
    const val TEACHER_NOTIFICATIONS = "teacher_notifications"
    const val TEACHER_REQUESTS = "teacher_requests"
    const val TEACHER_REQUEST_DETAIL = "teacher_request_detail/{applicationId}"
    const val TEACHER_REQUEST_DETAIL_ARG = "applicationId"
    const val TEACHER_STUDENTS = "teacher_students"

    const val TEACHER_STUDENT_DETAIL = "teacher_student_detail/{applicationId}/{studentProfileId}/{studentName}/{offerTitle}/{companyName}/{status}"
    const val TEACHER_STUDENT_DETAIL_APP_ID_ARG = "applicationId"
    const val TEACHER_STUDENT_DETAIL_PROFILE_ID_ARG = "studentProfileId"
    const val TEACHER_STUDENT_DETAIL_NAME_ARG = "studentName"
    const val TEACHER_STUDENT_DETAIL_OFFER_ARG = "offerTitle"
    const val TEACHER_STUDENT_DETAIL_COMPANY_ARG = "companyName"
    const val TEACHER_STUDENT_DETAIL_STATUS_ARG = "status"

    const val TEACHER_MESSAGES = "teacher_messages"
    const val TEACHER_PROFILE = "teacher_profile"
    const val TEACHER_EDIT_PROFILE = "teacher_edit_profile"

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

    const val STUDENT_SENT_ADVISOR_REQUESTS = "student_sent_advisor_requests"

    fun teacherRequestDetail(applicationId: String): String {
        return "teacher_request_detail/$applicationId"
    }

    fun teacherStudentDetail(
        applicationId: String,
        studentProfileId: String,
        studentName: String,
        offerTitle: String?,
        companyName: String?,
        status: String?
    ): String {
        return "teacher_student_detail/" +
                "$applicationId/" +
                "$studentProfileId/" +
                "${Uri.encode(studentName)}/" +
                "${Uri.encode(offerTitle ?: "na")}/" +
                "${Uri.encode(companyName ?: "na")}/" +
                "${Uri.encode(status ?: "na")}"
    }

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

    fun applicationChat(
        applicationId: String,
        name: String? = null,
        offerTitle: String? = null,
        studentProfileId: String? = null
    ): String {
        val builder = StringBuilder("application_chat/$applicationId?")
        name?.let { builder.append("name=${Uri.encode(it)}&") }
        offerTitle?.let { builder.append("offerTitle=${Uri.encode(it)}&") }
        studentProfileId?.let { builder.append("studentProfileId=${Uri.encode(it)}&") }
        return builder.toString().removeSuffix("&").removeSuffix("?")
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

    fun studentInternshipDetail(internshipId: String): String {
        return "student_internship_detail/$internshipId"
    }

    fun studentSearchAdvisor(internshipId: String): String {
        return "student_search_advisor/$internshipId"
    }

    fun companyProfile(companyProfileId: String): String {
        return "company_profile/$companyProfileId"
    }
}