package ir.dekot.fileto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ir.dekot.fileto.feature_compress.presentation.screen.MainScreen
import ir.dekot.fileto.ui.theme.FiletoTheme
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FiletoTheme { // نام تم را به FiletoTheme تغییر دهید
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // نمایش صفحه اصلی
                    MainScreen()
                }
            }
        }
    }
}
