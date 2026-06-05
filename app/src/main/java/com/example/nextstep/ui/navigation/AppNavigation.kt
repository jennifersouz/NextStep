package com.example.nextstep.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nextstep.data.local.AppPreferences
import com.example.nextstep.ui.screens.advisor.AdvisorDashboardScreen
import com.example.nextstep.ui.screens.auth.LoginScreen
import com.example.nextstep.ui.screens.auth.RegisterScreen
import com.example.nextstep.ui.screens.auth.UserRole
import com.example.nextstep.ui.screens.chat.ChatScreen
import com.example.nextstep.ui.screens.company.CompanyDashboardScreen
import com.example.nextstep.ui.screens.company.CompanyEditOfferScreen
import com.example.nextstep.ui.screens.company.CompanyOfferDetailScreen
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
                        Routes.chat(applicationId)
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
                }
            )
        }

        composable(Routes.STUDENT_SUBMITTED_APPLICATIONS) {
            StudentSubmittedApplicationsScreen(
                onBackClick = {
                    navController.navigateBackOr(Routes.STUDENT_DASHBOARD)
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
                    navController.navigateBackOr(Routes.STUDENT_DASHBOARD)
                },
                onMessagesClick = { selectedApplicationId ->
                    navController.navigate(
                        Routes.chat(selectedApplicationId)
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
                    navController.navigate(
                        Routes.companyOfferDetail(offerId)
                    )
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

            ChatScreen(
                applicationId = applicationId,
                onBackClick = {
                    navController.navigateBackOr(Routes.STUDENT_DASHBOARD)
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