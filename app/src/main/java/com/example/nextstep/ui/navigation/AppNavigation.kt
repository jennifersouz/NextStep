package com.example.nextstep.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nextstep.data.local.AppPreferences
import com.example.nextstep.ui.screens.advisor.AdvisorDashboardScreen
import com.example.nextstep.ui.screens.advisor.AdvisorEditProfileScreen
import com.example.nextstep.ui.screens.auth.LoginScreen
import com.example.nextstep.ui.screens.institution.AddInstitutionUserScreen
import com.example.nextstep.ui.screens.institution.InstitutionDashboardScreen
import com.example.nextstep.ui.screens.institution.InstitutionUsersScreen
import com.example.nextstep.ui.screens.institution.TeacherDashboardScreen
import com.example.nextstep.ui.screens.auth.RegisterScreen
import com.example.nextstep.ui.screens.auth.UserRole
import com.example.nextstep.ui.screens.chat.ApplicationChatScreen
import com.example.nextstep.ui.screens.company.AssignAdvisorScreen
import com.example.nextstep.ui.screens.company.CompanyApplicationDetailScreen
import com.example.nextstep.ui.screens.company.CompanyDashboardScreen
import com.example.nextstep.ui.screens.company.CompanyEditOfferScreen
import com.example.nextstep.ui.screens.company.CompanyOfferDetailScreen
import com.example.nextstep.ui.screens.company.CompanyStudentProfileScreen
import com.example.nextstep.ui.screens.intro.IntroScreen
import com.example.nextstep.ui.screens.splash.SplashScreen
import com.example.nextstep.ui.screens.student.StudentApplicationScreen
import com.example.nextstep.ui.screens.student.StudentDashboardScreen
import com.example.nextstep.ui.screens.student.StudentOfferDetailScreen
import com.example.nextstep.ui.screens.student.StudentSubmittedApplicationDetailScreen
import com.example.nextstep.ui.screens.student.StudentSubmittedApplicationsScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appPreferences = AppPreferences(context)

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen()

            LaunchedEffect(Unit) {
                delay(2000)

                val destination = if (appPreferences.isFirstLaunch()) {
                    Routes.INTRO
                } else {
                    Routes.LOGIN
                }

                navController.navigate(destination) {
                    popUpTo(Routes.SPLASH) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

        composable(Routes.INTRO) {
            IntroScreen(
                onFinish = {
                    appPreferences.setFirstLaunchCompleted()

                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.INTRO) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginClick = { role ->
                    val destination = when (role) {
                        UserRole.STUDENT -> Routes.STUDENT_DASHBOARD
                        UserRole.COMPANY -> Routes.COMPANY_DASHBOARD
                        UserRole.ADVISOR -> Routes.ADVISOR_DASHBOARD
                        UserRole.INSTITUTION -> Routes.INSTITUTION_DASHBOARD
                        UserRole.TEACHER -> Routes.TEACHER_DASHBOARD
                    }

                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.STUDENT_DASHBOARD) {
            StudentDashboardScreen(
                onOfferClick = { offerId ->
                    navController.navigate(
                        Routes.studentOfferDetail(offerId)
                    )
                },
                onSubmittedApplicationsClick = {
                    navController.navigate(
                        Routes.STUDENT_SUBMITTED_APPLICATIONS
                    )
                },
                onApplicationNotificationClick = { applicationId ->
                    navController.navigate(
                        Routes.studentSubmittedApplicationDetail(applicationId)
                    )
                },
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onChatClick = { applicationId ->
                    navController.navigate(
                        Routes.applicationChat(applicationId)
                    )
                }
            )
        }

        composable(Routes.ADVISOR_DASHBOARD) {
            AdvisorDashboardScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onChatClick = { applicationId ->
                    navController.navigate(
                        Routes.applicationChat(applicationId)
                    )
                },
                onEditProfileClick = {
                    navController.navigate(Routes.ADVISOR_EDIT_PROFILE)
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
                }
            )
        }

        composable(Routes.ADD_INSTITUTION_USER) {
            AddInstitutionUserScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.TEACHER_DASHBOARD) {
            TeacherDashboardScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.STUDENT_SUBMITTED_APPLICATIONS) {
            StudentSubmittedApplicationsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onApplicationClick = { applicationId ->
                    navController.navigate(
                        Routes.studentSubmittedApplicationDetail(applicationId)
                    )
                }
            )
        }

        composable(
            route = "${Routes.STUDENT_SUBMITTED_APPLICATION_DETAIL}/{${Routes.STUDENT_SUBMITTED_APPLICATION_DETAIL_ARG}}",
            arguments = listOf(
                navArgument(Routes.STUDENT_SUBMITTED_APPLICATION_DETAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments
                ?.getString(Routes.STUDENT_SUBMITTED_APPLICATION_DETAIL_ARG)
                .orEmpty()

            StudentSubmittedApplicationDetailScreen(
                applicationId = applicationId,
                onBackClick = {
                    navController.popBackStack()
                },
                onMessagesClick = { selectedApplicationId ->
                    Log.d("ChatNavigation", "Navigate chat applicationId=$selectedApplicationId")
                    navController.navigate(
                        Routes.applicationChat(selectedApplicationId)
                    )
                }
            )
        }

        composable(
            route = "${Routes.STUDENT_APPLICATION}/{${Routes.STUDENT_APPLICATION_ARG}}",
            arguments = listOf(
                navArgument(Routes.STUDENT_APPLICATION_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments
                ?.getString(Routes.STUDENT_APPLICATION_ARG)
                .orEmpty()

            StudentApplicationScreen(
                offerId = offerId,
                onBackClick = {
                    navController.navigateBackOr(Routes.STUDENT_DASHBOARD)
                }
            )
        }

        composable(
            route = "${Routes.STUDENT_OFFER_DETAIL}/{${Routes.STUDENT_OFFER_DETAIL_ARG}}",
            arguments = listOf(
                navArgument(Routes.STUDENT_OFFER_DETAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments
                ?.getString(Routes.STUDENT_OFFER_DETAIL_ARG)
                .orEmpty()

            StudentOfferDetailScreen(
                offerId = offerId,
                onBackClick = {
                    navController.navigateBackOr(Routes.STUDENT_DASHBOARD)
                },
                onApplyClick = { selectedOfferId ->
                    navController.navigate(
                        Routes.studentApplication(selectedOfferId)
                    )
                }
            )
        }

        composable(Routes.COMPANY_DASHBOARD) {
            CompanyDashboardScreen(
                onOfferClick = { offerId ->
                    navController.navigate(Routes.companyOfferDetail(offerId))
                },
                onApplicationClick = { applicationId ->
                    navController.navigate(Routes.companyApplicationDetail(applicationId))
                },
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.COMPANY_APPLICATION_DETAIL,
            arguments = listOf(
                navArgument(Routes.COMPANY_APPLICATION_DETAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments
                ?.getString(Routes.COMPANY_APPLICATION_DETAIL_ARG)
                .orEmpty()

            val advisorAssigned by backStackEntry.savedStateHandle
                .getStateFlow("advisor_assigned", false)
                .collectAsState()

            CompanyApplicationDetailScreen(
                applicationId = applicationId,
                advisorAssigned = advisorAssigned,
                onAdvisorAssignedConsumed = {
                    backStackEntry.savedStateHandle["advisor_assigned"] = false
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onStudentProfileClick = { selectedApplicationId ->
                    navController.navigate(
                        Routes.companyStudentProfile(selectedApplicationId)
                    )
                },
                onAssignAdvisorClick = { selectedApplicationId ->
                    navController.navigate(
                        Routes.companyAssignAdvisor(selectedApplicationId)
                    )
                }
            )
        }

        composable(
            route = Routes.COMPANY_OFFER_DETAIL,
            arguments = listOf(
                navArgument("offerId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments
                ?.getString("offerId")
                .orEmpty()

            CompanyOfferDetailScreen(
                offerId = offerId,
                onBackClick = {
                    navController.navigateBackOr(Routes.COMPANY_DASHBOARD)
                },
                onEditClick = { selectedOfferId ->
                    navController.navigate(
                        Routes.companyEditOffer(selectedOfferId)
                    )
                }
            )
        }

        composable(
            route = Routes.COMPANY_EDIT_OFFER,
            arguments = listOf(
                navArgument("offerId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments
                ?.getString("offerId")
                .orEmpty()

            CompanyEditOfferScreen(
                offerId = offerId,
                onBackClick = {
                    navController.navigateBackOr(Routes.COMPANY_DASHBOARD)
                },
                onOfferUpdated = {
                    navController.navigateBackOr(Routes.COMPANY_DASHBOARD)
                }
            )
        }

        composable(
            route = Routes.APPLICATION_CHAT,
            arguments = listOf(
                navArgument(Routes.APPLICATION_CHAT_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments
                ?.getString(Routes.APPLICATION_CHAT_ARG)
                .orEmpty()

            Log.d("ChatDebug", "Route chat applicationId=$applicationId")

            ApplicationChatScreen(
                applicationId = applicationId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(
                navArgument(Routes.CHAT_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments
                ?.getString(Routes.CHAT_ARG)
                .orEmpty()

            ApplicationChatScreen(
                applicationId = applicationId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.COMPANY_STUDENT_PROFILE,
            arguments = listOf(
                navArgument(Routes.COMPANY_STUDENT_PROFILE_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments
                ?.getString(Routes.COMPANY_STUDENT_PROFILE_ARG)
                .orEmpty()

            CompanyStudentProfileScreen(
                applicationId = applicationId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.COMPANY_ASSIGN_ADVISOR,
            arguments = listOf(
                navArgument(Routes.COMPANY_ASSIGN_ADVISOR_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments
                ?.getString(Routes.COMPANY_ASSIGN_ADVISOR_ARG)
                .orEmpty()

            AssignAdvisorScreen(
                applicationId = applicationId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAdvisorAssigned = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("advisor_assigned", true)

                    navController.popBackStack()
                }
            )
        }
    }
}

private fun NavController.navigateBackOr(
    fallbackRoute: String
) {
    val didPop = popBackStack()

    if (!didPop) {
        navigate(fallbackRoute) {
            launchSingleTop = true
        }
    }
}
