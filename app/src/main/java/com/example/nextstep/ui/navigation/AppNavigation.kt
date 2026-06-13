package com.example.nextstep.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nextstep.ui.screens.auth.LoginScreen
import com.example.nextstep.ui.screens.auth.RegisterScreen
import com.example.nextstep.ui.screens.auth.UserRole
import com.example.nextstep.ui.screens.company.CompanyDashboardScreen
import com.example.nextstep.ui.screens.company.CompanyOfferDetailScreen
import com.example.nextstep.ui.screens.company.CompanyEditOfferScreen
import com.example.nextstep.ui.screens.company.CompanyApplicationDetailScreen
import com.example.nextstep.ui.screens.company.CompanyStudentProfileScreen
import com.example.nextstep.ui.screens.company.CompanyInternStudentProfileScreen
import com.example.nextstep.ui.screens.company.CompanyProfileScreen
import com.example.nextstep.ui.screens.student.StudentDashboardScreen
import com.example.nextstep.ui.screens.student.StudentOfferDetailScreen
import com.example.nextstep.ui.screens.student.StudentApplicationScreen
import com.example.nextstep.ui.screens.student.StudentSubmittedApplicationsScreen
import com.example.nextstep.ui.screens.student.StudentSubmittedApplicationDetailScreen
import com.example.nextstep.ui.screens.student.StudentInternshipDetailScreen
import com.example.nextstep.ui.screens.student.StudentSearchAdvisorScreen
import com.example.nextstep.ui.screens.student.StudentSentAdvisorRequestsScreen
import com.example.nextstep.ui.screens.advisor.AdvisorDashboardScreen
import com.example.nextstep.ui.screens.advisor.AdvisorStudentDetailScreen
import com.example.nextstep.ui.screens.advisor.AdvisorEditProfileScreen
import com.example.nextstep.ui.screens.institution.InstitutionDashboardScreen
import com.example.nextstep.ui.screens.institution.AddInstitutionUserScreen
import com.example.nextstep.ui.screens.institution.InstitutionTeacherDetailScreen
import com.example.nextstep.ui.screens.institution.InstitutionStudentDetailScreen
import com.example.nextstep.ui.screens.institution.InstitutionUserDetailScreen
import com.example.nextstep.ui.screens.institution.InstitutionTeachersScreen
import com.example.nextstep.ui.screens.institution.InstitutionStudentsScreen
import com.example.nextstep.ui.screens.admin.AdminDashboardScreen
import com.example.nextstep.ui.screens.admin.AdminCreateUserScreen
import com.example.nextstep.ui.screens.chat.ApplicationChatScreen
import com.example.nextstep.ui.screens.teacher.TeacherDashboardScreen
import com.example.nextstep.ui.screens.teacher.TeacherRequestDetailScreen
import com.example.nextstep.ui.screens.teacher.TeacherStudentDetailScreen
import com.example.nextstep.ui.screens.teacher.TeacherEditProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { role ->
                    val destination = when (role) {
                        UserRole.STUDENT -> Routes.STUDENT_DASHBOARD
                        UserRole.COMPANY -> Routes.COMPANY_DASHBOARD
                        UserRole.ADVISOR -> Routes.ADVISOR_DASHBOARD
                        UserRole.INSTITUTION -> Routes.INSTITUTION_DASHBOARD
                        UserRole.TEACHER -> Routes.TEACHER_DASHBOARD
                        UserRole.ADMIN -> Routes.ADMIN_DASHBOARD
                        else -> Routes.LOGIN
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.STUDENT_DASHBOARD) {
            StudentDashboardScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onOfferClick = { offerId ->
                    navController.navigate("${Routes.STUDENT_OFFER_DETAIL}/$offerId")
                },
                onSubmittedApplicationsClick = {
                    navController.navigate(Routes.STUDENT_SUBMITTED_APPLICATIONS)
                },
                onInternshipClick = { internshipId ->
                    navController.navigate(Routes.studentInternshipDetail(internshipId))
                },
                onSentRequestsClick = {
                    navController.navigate(Routes.STUDENT_SENT_ADVISOR_REQUESTS)
                }
            )
        }

        composable(
            route = "${Routes.STUDENT_OFFER_DETAIL}/{${Routes.STUDENT_OFFER_DETAIL_ARG}}",
            arguments = listOf(
                navArgument(Routes.STUDENT_OFFER_DETAIL_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString(Routes.STUDENT_OFFER_DETAIL_ARG).orEmpty()
            StudentOfferDetailScreen(
                offerId = offerId,
                onBackClick = { navController.popBackStack() },
                onApplyClick = {
                    navController.navigate("${Routes.STUDENT_APPLICATION}/$offerId")
                }
            )
        }

        composable(
            route = "${Routes.STUDENT_APPLICATION}/{${Routes.STUDENT_APPLICATION_ARG}}",
            arguments = listOf(
                navArgument(Routes.STUDENT_APPLICATION_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString(Routes.STUDENT_APPLICATION_ARG).orEmpty()
            StudentApplicationScreen(
                offerId = offerId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.STUDENT_SUBMITTED_APPLICATIONS) {
            StudentSubmittedApplicationsScreen(
                onBackClick = { navController.popBackStack() },
                onApplicationClick = { applicationId ->
                    navController.navigate(Routes.studentSubmittedApplicationDetail(applicationId))
                }
            )
        }

        composable(
            route = "${Routes.STUDENT_SUBMITTED_APPLICATION_DETAIL}/{${Routes.STUDENT_SUBMITTED_APPLICATION_DETAIL_ARG}}",
            arguments = listOf(
                navArgument(Routes.STUDENT_SUBMITTED_APPLICATION_DETAIL_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Routes.STUDENT_SUBMITTED_APPLICATION_DETAIL_ARG).orEmpty()
            StudentSubmittedApplicationDetailScreen(
                applicationId = applicationId,
                onBackClick = { navController.popBackStack() },
                onMessagesClick = {
                    navController.navigate(Routes.applicationChat(applicationId))
                }
            )
        }

        composable(
            route = "${Routes.STUDENT_INTERNSHIP_DETAIL}/{${Routes.STUDENT_INTERNSHIP_DETAIL_ARG}}",
            arguments = listOf(
                navArgument(Routes.STUDENT_INTERNSHIP_DETAIL_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val internshipId = backStackEntry.arguments?.getString(Routes.STUDENT_INTERNSHIP_DETAIL_ARG).orEmpty()
            StudentInternshipDetailScreen(
                internshipId = internshipId,
                onBackClick = { navController.popBackStack() },
                onChatClick = { participantId, participantName ->
                    navController.navigate(Routes.applicationChat(participantId, participantName))
                },
                onSearchAdvisorClick = {
                    navController.navigate(Routes.studentSearchAdvisor(internshipId))
                }
            )
        }

        composable(
            route = Routes.STUDENT_SEARCH_ADVISOR,
            arguments = listOf(
                navArgument(Routes.STUDENT_SEARCH_ADVISOR_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val internshipId = backStackEntry.arguments?.getString(Routes.STUDENT_SEARCH_ADVISOR_ARG).orEmpty()
            StudentSearchAdvisorScreen(
                internshipId = internshipId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.STUDENT_SENT_ADVISOR_REQUESTS) {
            StudentSentAdvisorRequestsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.COMPANY_DASHBOARD) {
            CompanyDashboardScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOfferClick = { offerId ->
                    navController.navigate(Routes.companyOfferDetail(offerId))
                },
                onApplicationClick = { applicationId ->
                    navController.navigate(Routes.companyApplicationDetail(applicationId))
                }
            )
        }

        composable(
            route = Routes.COMPANY_OFFER_DETAIL,
            arguments = listOf(
                navArgument("offerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString("offerId").orEmpty()
            CompanyOfferDetailScreen(
                offerId = offerId,
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate(Routes.companyEditOffer(offerId))
                }
            )
        }

        composable(
            route = Routes.COMPANY_EDIT_OFFER,
            arguments = listOf(
                navArgument("offerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString("offerId").orEmpty()
            CompanyEditOfferScreen(
                offerId = offerId,
                onBackClick = { navController.popBackStack() },
                onOfferUpdated = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.COMPANY_APPLICATION_DETAIL,
            arguments = listOf(
                navArgument(Routes.COMPANY_APPLICATION_DETAIL_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Routes.COMPANY_APPLICATION_DETAIL_ARG).orEmpty()
            CompanyApplicationDetailScreen(
                applicationId = applicationId,
                onBackClick = { navController.popBackStack() },
                onAssignAdvisorClick = {
                    // Rota company_assign_advisor foi removida (tela não existe)
                },
                onStudentProfileClick = {
                    navController.navigate(Routes.companyStudentProfile(applicationId))
                }
            )
        }

        composable(
            route = Routes.COMPANY_STUDENT_PROFILE,
            arguments = listOf(
                navArgument(Routes.COMPANY_STUDENT_PROFILE_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Routes.COMPANY_STUDENT_PROFILE_ARG).orEmpty()
            CompanyStudentProfileScreen(
                applicationId = applicationId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.COMPANY_INTERN_STUDENT_PROFILE,
            arguments = listOf(
                navArgument(Routes.COMPANY_INTERN_STUDENT_PROFILE_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Routes.COMPANY_INTERN_STUDENT_PROFILE_ARG).orEmpty()
            CompanyInternStudentProfileScreen(
                applicationId = applicationId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.COMPANY_PROFILE,
            arguments = listOf(
                navArgument(Routes.COMPANY_PROFILE_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val companyProfileId = backStackEntry.arguments?.getString(Routes.COMPANY_PROFILE_ARG).orEmpty()
            CompanyProfileScreen(
                companyProfileId = companyProfileId,
                onBackClick = { navController.popBackStack() },
                onOfferClick = { offerId ->
                    navController.navigate(Routes.companyOfferDetail(offerId))
                }
            )
        }

        composable(
            route = Routes.APPLICATION_CHAT,
            arguments = listOf(
                navArgument(Routes.APPLICATION_CHAT_ARG) { type = NavType.StringType },
                navArgument(Routes.APPLICATION_CHAT_NAME_ARG) { type = NavType.StringType; nullable = true },
                navArgument(Routes.APPLICATION_CHAT_OFFER_ARG) { type = NavType.StringType; nullable = true },
                navArgument(Routes.APPLICATION_CHAT_STUDENT_ID_ARG) { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Routes.APPLICATION_CHAT_ARG).orEmpty()
            val name = backStackEntry.arguments?.getString(Routes.APPLICATION_CHAT_NAME_ARG)
            val offerTitle = backStackEntry.arguments?.getString(Routes.APPLICATION_CHAT_OFFER_ARG)
            val studentProfileId = backStackEntry.arguments?.getString(Routes.APPLICATION_CHAT_STUDENT_ID_ARG)

            ApplicationChatScreen(
                applicationId = applicationId,
                participantName = name,
                offerTitle = offerTitle,
                studentProfileId = studentProfileId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.ADVISOR_DASHBOARD) {
            AdvisorDashboardScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onStudentClick = { applicationId ->
                    navController.navigate(Routes.advisorStudentDetail(applicationId))
                },
                onEditProfileClick = {
                    navController.navigate(Routes.ADVISOR_EDIT_PROFILE)
                }
            )
        }

        composable(
            route = Routes.ADVISOR_STUDENT_DETAIL,
            arguments = listOf(
                navArgument(Routes.ADVISOR_STUDENT_DETAIL_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Routes.ADVISOR_STUDENT_DETAIL_ARG).orEmpty()
            AdvisorStudentDetailScreen(
                applicationId = applicationId,
                onBackClick = {
                    navController.popBackStack()
                },
                onMessageClick = {
                    navController.navigate(
                        Routes.applicationChat(applicationId)
                    )
                },
                onEvaluateClick = {
                    // Rota advisor_evaluate_student foi removida (tela não existe)
                }
            )
        }

        composable(Routes.ADVISOR_EDIT_PROFILE) {
            AdvisorEditProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.INSTITUTION_DASHBOARD) {
            InstitutionDashboardScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onAddUserClick = {
                    navController.navigate(Routes.ADD_INSTITUTION_USER)
                },
                onTeacherClick = { teacherProfileId ->
                    navController.navigate(Routes.INSTITUTION_TEACHER_DETAIL.replace("{teacherProfileId}", teacherProfileId))
                },
                onStudentClick = { studentProfileId ->
                    navController.navigate(Routes.INSTITUTION_STUDENT_DETAIL.replace("{studentProfileId}", studentProfileId))
                },
                onUserClick = { profileId, role, inviteId, isAccepted ->
                    if (isAccepted && !profileId.isNullOrBlank()) {
                        when (role.lowercase()) {
                            "student" -> navController.navigate(
                                Routes.INSTITUTION_STUDENT_DETAIL.replace("{studentProfileId}", profileId)
                            )
                            "teacher" -> navController.navigate(
                                Routes.INSTITUTION_TEACHER_DETAIL.replace("{teacherProfileId}", profileId)
                            )
                            else -> navController.navigate(
                                Routes.institutionUserDetail(profileId, role, inviteId)
                            )
                        }
                    } else {
                        navController.navigate(
                            Routes.institutionUserDetail(profileId, role, inviteId)
                        )
                    }
                }
            )
        }

        composable(Routes.INSTITUTION_TEACHERS) {
            InstitutionTeachersScreen(
                onTeacherClick = { teacherProfileId ->
                    navController.navigate(Routes.INSTITUTION_TEACHER_DETAIL.replace("{teacherProfileId}", teacherProfileId))
                }
            )
        }

        composable(
            route = Routes.INSTITUTION_TEACHER_DETAIL,
            arguments = listOf(
                navArgument(Routes.INSTITUTION_TEACHER_DETAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val teacherProfileId = backStackEntry.arguments
                ?.getString(Routes.INSTITUTION_TEACHER_DETAIL_ARG)
                .orEmpty()

            InstitutionTeacherDetailScreen(
                teacherProfileId = teacherProfileId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.INSTITUTION_STUDENTS) {
            InstitutionStudentsScreen(
                onStudentClick = { studentProfileId ->
                    navController.navigate(Routes.INSTITUTION_STUDENT_DETAIL.replace("{studentProfileId}", studentProfileId))
                }
            )
        }

        composable(
            route = Routes.INSTITUTION_STUDENT_DETAIL,
            arguments = listOf(
                navArgument(Routes.INSTITUTION_STUDENT_DETAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val studentProfileId = backStackEntry.arguments
                ?.getString(Routes.INSTITUTION_STUDENT_DETAIL_ARG)
                .orEmpty()

            InstitutionStudentDetailScreen(
                studentProfileId = studentProfileId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.INSTITUTION_USER_DETAIL,
            arguments = listOf(
                navArgument(Routes.INSTITUTION_USER_DETAIL_PROFILE_ARG) {
                    type = NavType.StringType
                },
                navArgument(Routes.INSTITUTION_USER_DETAIL_ROLE_ARG) {
                    type = NavType.StringType
                },
                navArgument(Routes.INSTITUTION_USER_DETAIL_INVITE_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments
                ?.getString(Routes.INSTITUTION_USER_DETAIL_PROFILE_ARG)
                .orEmpty()
            val role = backStackEntry.arguments
                ?.getString(Routes.INSTITUTION_USER_DETAIL_ROLE_ARG)
                .orEmpty()
            val inviteId = backStackEntry.arguments
                ?.getString(Routes.INSTITUTION_USER_DETAIL_INVITE_ARG)
                .orEmpty()

            InstitutionUserDetailScreen(
                profileId = profileId,
                role = role,
                inviteId = inviteId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.ADD_INSTITUTION_USER) {
            AddInstitutionUserScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onAddUserClick = {
                    navController.navigate(Routes.ADMIN_CREATE_USER)
                }
            )
        }

        composable(Routes.ADMIN_CREATE_USER) {
            AdminCreateUserScreen(
                onBackClick = { navController.popBackStack() },
                onUserCreated = { navController.popBackStack() }
            )
        }

        composable(Routes.TEACHER_DASHBOARD) {
            TeacherDashboardScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRequestClick = { applicationId ->
                    navController.navigate(Routes.teacherRequestDetail(applicationId))
                },
                onStudentClick = { student ->
                    navController.navigate(
                        Routes.teacherStudentDetail(
                            applicationId = student.applicationId,
                            studentProfileId = student.studentProfileId ?: "",
                            studentName = student.studentName ?: "",
                            offerTitle = student.offerTitle,
                            companyName = student.companyName,
                            status = student.status
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.TEACHER_REQUEST_DETAIL,
            arguments = listOf(
                navArgument(Routes.TEACHER_REQUEST_DETAIL_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Routes.TEACHER_REQUEST_DETAIL_ARG).orEmpty()
            TeacherRequestDetailScreen(
                applicationId = applicationId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TEACHER_STUDENT_DETAIL,
            arguments = listOf(
                navArgument(Routes.TEACHER_STUDENT_DETAIL_APP_ID_ARG) { type = NavType.StringType },
                navArgument(Routes.TEACHER_STUDENT_DETAIL_PROFILE_ID_ARG) { type = NavType.StringType },
                navArgument(Routes.TEACHER_STUDENT_DETAIL_NAME_ARG) { type = NavType.StringType },
                navArgument(Routes.TEACHER_STUDENT_DETAIL_OFFER_ARG) { type = NavType.StringType },
                navArgument(Routes.TEACHER_STUDENT_DETAIL_COMPANY_ARG) { type = NavType.StringType },
                navArgument(Routes.TEACHER_STUDENT_DETAIL_STATUS_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val appId = backStackEntry.arguments?.getString(Routes.TEACHER_STUDENT_DETAIL_APP_ID_ARG).orEmpty()
            val profileId = backStackEntry.arguments?.getString(Routes.TEACHER_STUDENT_DETAIL_PROFILE_ID_ARG).orEmpty()
            val studentName = backStackEntry.arguments?.getString(Routes.TEACHER_STUDENT_DETAIL_NAME_ARG).orEmpty()
            val offerTitle = backStackEntry.arguments?.getString(Routes.TEACHER_STUDENT_DETAIL_OFFER_ARG).orEmpty()
            val companyName = backStackEntry.arguments?.getString(Routes.TEACHER_STUDENT_DETAIL_COMPANY_ARG).orEmpty()
            val status = backStackEntry.arguments?.getString(Routes.TEACHER_STUDENT_DETAIL_STATUS_ARG).orEmpty()

            TeacherStudentDetailScreen(
                applicationId = appId,
                studentProfileId = profileId,
                initialStudentName = studentName,
                initialOfferTitle = offerTitle,
                initialCompanyName = companyName,
                status = status,
                onBackClick = { navController.popBackStack() },
                onMessageClick = {
                    navController.navigate(Routes.applicationChat(appId))
                }
            )
        }

        composable(Routes.TEACHER_EDIT_PROFILE) {
            TeacherEditProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}