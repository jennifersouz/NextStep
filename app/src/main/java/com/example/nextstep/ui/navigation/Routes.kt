package com.example.nextstep.ui.navigation

import android.net.Uri

object Routes {
    const val SPLASH = "splash"
    const val INTRO = "intro"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val STUDENT_DASHBOARD = "student_dashboard"
    const val COMPANY_DASHBOARD = "company_dashboard"
    const val ADVISOR_DASHBOARD = "advisor_dashboard"
    const val INSTITUTION_DASHBOARD = "institution_dashboard"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val TEACHER_DASHBOARD = "teacher_dashboard"

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

    const val STUDENT_SENT_ADVISOR_REQUESTS = "student_sent_advisor_requests"

    const val PROFILE_DETAIL = "profile_detail/{profileId}/{type}"
    const val PROFILE_DETAIL_PROFILE_ID_ARG = "profileId"
    const val PROFILE_DETAIL_TYPE_ARG = "type"

    const val COMPANY_PROFILE = "company_profile/{companyProfileId}"
    const val COMPANY_PROFILE_ARG = "companyProfileId"

    const val COMPANY_OFFER_DETAIL = "company_offer_detail/{offerId}"
    const val COMPANY_OFFER_DETAIL_ARG = "offerId"

    const val COMPANY_EDIT_OFFER = "company_edit_offer/{offerId}"
    const val COMPANY_EDIT_OFFER_ARG = "offerId"

    const val COMPANY_APPLICATION_DETAIL = "company_application_detail/{applicationId}"
    const val COMPANY_APPLICATION_DETAIL_ARG = "applicationId"

    const val COMPANY_STUDENT_PROFILE = "company_student_profile/{applicationId}"
    const val COMPANY_STUDENT_PROFILE_ARG = "applicationId"

    const val COMPANY_INTERN_STUDENT_PROFILE = "company_intern_student_profile/{applicationId}"
    const val COMPANY_INTERN_STUDENT_PROFILE_ARG = "applicationId"

    const val COMPANY_ASSIGN_ADVISOR = "company_assign_advisor/{applicationId}"
    const val COMPANY_ASSIGN_ADVISOR_ARG = "applicationId"

    const val CHAT = "chat/{applicationId}"
    const val CHAT_ARG = "applicationId"

    const val APPLICATION_CHAT =
        "application_chat/{applicationId}?name={name}&offerTitle={offerTitle}&studentProfileId={studentProfileId}"

    const val APPLICATION_CHAT_ARG = "applicationId"
    const val APPLICATION_CHAT_NAME_ARG = "name"
    const val APPLICATION_CHAT_OFFER_ARG = "offerTitle"
    const val APPLICATION_CHAT_STUDENT_ID_ARG = "studentProfileId"

    const val ADVISOR_EDIT_PROFILE = "advisor_edit_profile"

    const val ADVISOR_STUDENT_DETAIL = "advisor_student_detail/{applicationId}"
    const val ADVISOR_STUDENT_DETAIL_ARG = "applicationId"

    const val ADVISOR_EVALUATE_STUDENT = "advisor_evaluate_student/{applicationId}"
    const val ADVISOR_EVALUATE_STUDENT_ARG = "applicationId"

    const val ADVISOR_NOTIFICATIONS = "advisor_notifications"

    const val INSTITUTION_USERS = "institution_users"
    const val ADD_INSTITUTION_USER = "add_institution_user"

    const val INSTITUTION_TEACHERS = "institution_teachers"
    const val INSTITUTION_TEACHER_DETAIL = "institution_teacher_detail/{teacherProfileId}"
    const val INSTITUTION_TEACHER_DETAIL_ARG = "teacherProfileId"

    const val INSTITUTION_STUDENTS = "institution_students"
    const val INSTITUTION_STUDENT_DETAIL = "institution_student_detail/{studentProfileId}"
    const val INSTITUTION_STUDENT_DETAIL_ARG = "studentProfileId"

    const val INSTITUTION_USER_DETAIL = "institution_user_detail/{profileId}/{role}/{inviteId}"
    const val INSTITUTION_USER_DETAIL_PROFILE_ARG = "profileId"
    const val INSTITUTION_USER_DETAIL_ROLE_ARG = "role"
    const val INSTITUTION_USER_DETAIL_INVITE_ARG = "inviteId"

    const val ADMIN_CREATE_USER = "admin_create_user"

    const val TEACHER_NOTIFICATIONS = "teacher_notifications"
    const val TEACHER_REQUESTS = "teacher_requests"

    const val TEACHER_REQUEST_DETAIL = "teacher_request_detail/{applicationId}"
    const val TEACHER_REQUEST_DETAIL_ARG = "applicationId"

    const val TEACHER_STUDENTS = "teacher_students"

    const val TEACHER_STUDENT_DETAIL =
        "teacher_student_detail/{applicationId}/{studentProfileId}/{studentName}/{offerTitle}/{companyName}/{status}"

    const val TEACHER_STUDENT_DETAIL_APP_ID_ARG = "applicationId"
    const val TEACHER_STUDENT_DETAIL_PROFILE_ID_ARG = "studentProfileId"
    const val TEACHER_STUDENT_DETAIL_NAME_ARG = "studentName"
    const val TEACHER_STUDENT_DETAIL_OFFER_ARG = "offerTitle"
    const val TEACHER_STUDENT_DETAIL_COMPANY_ARG = "companyName"
    const val TEACHER_STUDENT_DETAIL_STATUS_ARG = "status"

    const val TEACHER_MESSAGES = "teacher_messages"
    const val TEACHER_PROFILE = "teacher_profile"
    const val TEACHER_EDIT_PROFILE = "teacher_edit_profile"

    fun studentOfferDetail(offerId: String): String {
        return "$STUDENT_OFFER_DETAIL/${Uri.encode(offerId)}"
    }

    fun studentApplication(offerId: String): String {
        return "$STUDENT_APPLICATION/${Uri.encode(offerId)}"
    }

    fun studentSubmittedApplicationDetail(applicationId: String): String {
        return "$STUDENT_SUBMITTED_APPLICATION_DETAIL/${Uri.encode(applicationId)}"
    }

    fun studentInternshipDetail(internshipId: String): String {
        return "$STUDENT_INTERNSHIP_DETAIL/${Uri.encode(internshipId)}"
    }

    fun studentSearchAdvisor(internshipId: String): String {
        return "student_search_advisor/${Uri.encode(internshipId)}"
    }

    fun companyProfile(companyProfileId: String): String {
        return "company_profile/${Uri.encode(companyProfileId)}"
    }

    fun companyOfferDetail(offerId: String): String {
        return "company_offer_detail/${Uri.encode(offerId)}"
    }

    fun companyEditOffer(offerId: String): String {
        return "company_edit_offer/${Uri.encode(offerId)}"
    }

    fun companyApplicationDetail(applicationId: String): String {
        return "company_application_detail/${Uri.encode(applicationId)}"
    }

    fun companyAssignAdvisor(applicationId: String): String {
        return "company_assign_advisor/${Uri.encode(applicationId)}"
    }

    fun companyStudentProfile(applicationId: String): String {
        return "company_student_profile/${Uri.encode(applicationId)}"
    }

    fun companyInternStudentProfile(applicationId: String): String {
        return "company_intern_student_profile/${Uri.encode(applicationId)}"
    }

    fun chat(applicationId: String): String {
        return "chat/${Uri.encode(applicationId)}"
    }

    fun applicationChat(
        applicationId: String,
        participantName: String? = null,
        offerTitle: String? = null,
        studentProfileId: String? = null
    ): String {
        val queryParams = mutableListOf<String>()

        if (!participantName.isNullOrBlank()) {
            queryParams.add("name=${Uri.encode(participantName)}")
        }

        if (!offerTitle.isNullOrBlank()) {
            queryParams.add("offerTitle=${Uri.encode(offerTitle)}")
        }

        if (!studentProfileId.isNullOrBlank()) {
            queryParams.add("studentProfileId=${Uri.encode(studentProfileId)}")
        }

        val query = if (queryParams.isEmpty()) {
            ""
        } else {
            "?" + queryParams.joinToString("&")
        }

        return "application_chat/${Uri.encode(applicationId)}$query"
    }

    fun advisorStudentDetail(applicationId: String): String {
        return "advisor_student_detail/${Uri.encode(applicationId)}"
    }

    fun advisorEvaluateStudent(applicationId: String): String {
        return "advisor_evaluate_student/${Uri.encode(applicationId)}"
    }

    fun teacherRequestDetail(applicationId: String): String {
        return "teacher_request_detail/${Uri.encode(applicationId)}"
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
                "${Uri.encode(applicationId)}/" +
                "${Uri.encode(studentProfileId)}/" +
                "${Uri.encode(studentName)}/" +
                "${Uri.encode(offerTitle ?: "na")}/" +
                "${Uri.encode(companyName ?: "na")}/" +
                Uri.encode(status ?: "na")
    }

    fun profileDetail(
        profileId: String,
        type: String
    ): String {
        return "profile_detail/${Uri.encode(profileId)}/${Uri.encode(type)}"
    }

    fun institutionUserDetail(
        profileId: String?,
        role: String,
        inviteId: String?
    ): String {
        val safeProfileId = profileId
            ?.takeIf { it.isNotBlank() }
            ?: "no_profile"

        val safeRole = role
            .takeIf { it.isNotBlank() }
            ?: "unknown"

        val safeInviteId = inviteId
            ?.takeIf { it.isNotBlank() }
            ?: "no_invite"

        return "institution_user_detail/" +
                "${Uri.encode(safeProfileId)}/" +
                "${Uri.encode(safeRole)}/" +
                Uri.encode(safeInviteId)
    }
}