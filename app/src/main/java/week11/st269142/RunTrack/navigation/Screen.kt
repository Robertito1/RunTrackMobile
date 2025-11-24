package week11.st269142.RunTrack.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Feature : Screen("feature")
    object RunHistory : Screen("run_history")
}
