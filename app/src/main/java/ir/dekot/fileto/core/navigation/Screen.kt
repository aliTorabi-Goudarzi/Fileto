package ir.dekot.fileto.core.navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object HistoryScreen : Screen("history_screen")
    object SettingsScreen : Screen("settings_screen")
    // مسیر جدید اضافه شد
    object CreatePdfScreen : Screen("create_pdf_screen")
    // ابزارهای PDF
    object PdfToolsScreen : Screen("pdf_tools_screen")
    object SplitPdfScreen : Screen("split_pdf_screen")
    object MergePdfScreen : Screen("merge_pdf_screen")
}