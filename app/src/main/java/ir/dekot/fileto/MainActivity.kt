package ir.dekot.fileto

import android.content.Context
import android.os.Bundle
import androidx.compose.ui.unit.LayoutDirection
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import ir.dekot.fileto.core.di.LocaleEntryPoint
import ir.dekot.fileto.core.navigation.Screen
import ir.dekot.fileto.core.utils.updateLocale
import ir.dekot.fileto.feature_compress.presentation.screen.MainScreen
import ir.dekot.fileto.feature_create_pdf.presentation.screen.CreatePdfScreen
import ir.dekot.fileto.feature_history.presentation.screen.HistoryScreen
import ir.dekot.fileto.feature_pdf_tools.presentation.screen.PdfToolsScreen
import ir.dekot.fileto.feature_pdf_tools.presentation.screen.SplitPdfScreen
import ir.dekot.fileto.feature_settings.domain.model.Theme
import ir.dekot.fileto.feature_settings.presentation.screen.SettingsScreen
import ir.dekot.fileto.ui.theme.FiletoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private var currentLocale: Locale? = null

    override fun attachBaseContext(newBase: Context) {
        // این متد قبل از تزریق وابستگی‌های Hilt به Activity فراخوانی می‌شود.
        // برای دسترسی به Repository، از یک EntryPoint استفاده می‌کنیم.
        val entryPoint = EntryPointAccessors.fromApplication(newBase, LocaleEntryPoint::class.java)
        val settingsRepository = entryPoint.settingsRepository()

        // از runBlocking استفاده می‌کنیم چون این کار باید به صورت همزمان (synchronously)
        // قبل از ادامه کار Activity انجام شود. خواندن از DataStore بسیار سریع است.
        val languageCode = runBlocking { settingsRepository.getLanguage().first().code }
        @Suppress("DEPRECATION") val locale = Locale(languageCode)
        currentLocale = locale
        super.attachBaseContext(newBase.updateLocale(locale))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme by viewModel.theme.collectAsState()
            val language by viewModel.language.collectAsState()

            // اگر کاربر زبان را در تنظیمات تغییر دهد، state مربوط به language به‌روز می‌شود.
            // ما آن را با زبان فعلی Activity مقایسه می‌کنیم و در صورت تفاوت،
            // Activity را دوباره می‌سازیم تا زبان جدید اعمال شود.
            if (currentLocale?.language != language.code) {
                recreate()
            }

            val darkTheme = when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            val layoutDirection =
                if (language.code == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                FiletoTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = Screen.MainScreen.route
                        ) {
                            composable(route = Screen.MainScreen.route) {
                                MainScreen(navController = navController)
                            }
                            composable(route = Screen.HistoryScreen.route) {
                                HistoryScreen(navController = navController)
                            }
                            composable(route = Screen.SettingsScreen.route) {
                                SettingsScreen(navController = navController)
                            }
                            // روت جدید اضافه شد
                            composable(route = Screen.CreatePdfScreen.route) {
                                CreatePdfScreen(navController = navController)
                            }
                            // ابزارهای PDF
                            composable(route = Screen.PdfToolsScreen.route) {
                                PdfToolsScreen(navController = navController)
                            }
                            // تقسیم PDF
                            composable(route = Screen.SplitPdfScreen.route) {
                                SplitPdfScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
