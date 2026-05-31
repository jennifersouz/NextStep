package com.example.nextstep.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nextstep.data.local.AppPreferences
import com.example.nextstep.ui.screens.auth.LoginScreen
import com.example.nextstep.ui.screens.auth.RegisterScreen
import com.example.nextstep.ui.screens.auth.UserRole
import com.example.nextstep.ui.screens.company.CompanyDashboardScreen
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
                    }

                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
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
                    }
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.STUDENT_DASHBOARD) {
            StudentDashboardScreen(
                onOfferClick = { offerId ->
                    navController.navigate(Routes.studentOfferDetail(offerId))
                },
                onSubmittedApplicationsClick = {
                    navController.navigate(Routes.STUDENT_SUBMITTED_APPLICATIONS)
                },
                onApplicationNotificationClick = { applicationId ->
                    navController.navigate(
                        Routes.studentSubmittedApplicationDetail(applicationId)
                    )
                }
            )
        }

        composable(Routes.STUDENT_SUBMITTED_APPLICATIONS) {
            StudentSubmittedApplicationsScreen(
                onBackClick = {
                    // Fallback para dashboard se a stack estiver vazia
                    navController.safePopBack(Routes.STUDENT_DASHBOARD)
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
                    // Pode ter sido aberto a partir da lista OU de uma notificação
                    // (que navega direto para o detalhe sem passar pela lista).
                    // safePopBack garante que nunca fecha a app.
                    navController.safePopBack(Routes.STUDENT_DASHBOARD)
                }
            )
        }

        composable(Routes.COMPANY_DASHBOARD) {
            CompanyDashboardScreen(
                onOfferClick = { offerId ->
                    navController.navigate(Routes.studentOfferDetail(offerId))
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
            val applicationOfferId = backStackEntry.arguments
                ?.getString(Routes.STUDENT_APPLICATION_ARG)
                .orEmpty()

            StudentApplicationScreen(
                offerId = applicationOfferId,
                onBackClick = {
                    // Volta para o detalhe da oferta (que está na stack)
                    // ou para o dashboard se a stack estiver vazia
                    navController.safePopBack(Routes.STUDENT_DASHBOARD)
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
                    // Pode ter sido aberto a partir do dashboard do aluno,
                    // dos estágios guardados, ou do dashboard da empresa.
                    // safePopBack volta para quem chamou, ou para o dashboard
                    // correto se a stack estiver vazia.
                    navController.safePopBack(Routes.STUDENT_DASHBOARD)
                },
                onApplyClick = { selectedOfferId ->
                    navController.navigate(Routes.studentApplication(selectedOfferId))
                }
            )
        }
    }
}
