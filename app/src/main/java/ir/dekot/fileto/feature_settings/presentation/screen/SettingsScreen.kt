package ir.dekot.fileto.feature_settings.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.dekot.fileto.R
import ir.dekot.fileto.feature_settings.domain.model.Language
import ir.dekot.fileto.feature_settings.domain.model.Theme
import ir.dekot.fileto.feature_settings.presentation.components.ThemeRadioButton
import ir.dekot.fileto.feature_settings.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val selectedTheme by viewModel.theme.collectAsState()
    val selectedLanguage by viewModel.language.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(stringResource(id = R.string.select_theme), style = MaterialTheme.typography.titleLarge)
            Column(Modifier.selectableGroup()) {
                ThemeRadioButton(
                    text = stringResource(id = R.string.light_theme),
                    selected = selectedTheme == Theme.LIGHT,
                    onClick = { viewModel.onThemeChange(Theme.LIGHT) }
                )
                ThemeRadioButton(
                    text = stringResource(id = R.string.dark_theme),
                    selected = selectedTheme == Theme.DARK,
                    onClick = { viewModel.onThemeChange(Theme.DARK) }
                )
                ThemeRadioButton(
                    text = stringResource(id = R.string.system_default_theme),
                    selected = selectedTheme == Theme.SYSTEM,
                    onClick = { viewModel.onThemeChange(Theme.SYSTEM) }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            Text(stringResource(id = R.string.select_language), style = MaterialTheme.typography.titleLarge)
            Column(Modifier.selectableGroup()) {
                Language.entries.forEach { language ->
                    // <<-- تغییر اصلی اینجاست -->>
                    // منطق نمایش به لایه UI منتقل شد
                    val languageText = when (language) {
                        Language.PERSIAN -> stringResource(id = R.string.language_persian)
                        Language.ENGLISH -> stringResource(id = R.string.language_english)
                    }
                    ThemeRadioButton(
                        text = languageText,
                        selected = selectedLanguage == language,
                        onClick = { viewModel.onLanguageChange(language) }
                    )
                }
            }
        }
    }
}