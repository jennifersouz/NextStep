package com.example.nextstep.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nextstep.data.local.AppPreferences
import com.example.nextstep.ui.screens.auth.LoginScreen
import com.example.nextstep.ui.screens.auth.RegisterScreen
import com.example.nextstep.ui.screens.intro.IntroScreen
import com.example.nextstep.ui.screens.splash.SplashScreen
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
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}