package ir.dekot.fileto.core.navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object HistoryScreen : Screen("history_screen")
    object SettingsScreen : Screen("settings_screen")
}