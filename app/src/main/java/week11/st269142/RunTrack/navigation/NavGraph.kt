package week11.st269142.RunTrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import week11.st269142.RunTrack.data.AuthRepository
import week11.st269142.RunTrack.ui.screen.FeatureScreen
import week11.st269142.RunTrack.ui.screen.ForgotPasswordScreen
import week11.st269142.RunTrack.ui.screen.HomeScreen
import week11.st269142.RunTrack.ui.screen.LoginScreen
import week11.st269142.RunTrack.ui.screen.RegisterScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    val startDestination = if (authRepository.isUserSignedIn()) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Flow
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Main App Flow
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToFeature = {
                    navController.navigate(Screen.Feature.route)
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Feature.route) {
            FeatureScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
