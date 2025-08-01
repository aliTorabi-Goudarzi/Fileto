package ir.dekot.fileto.feature_settings.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.dekot.fileto.feature_settings.domain.model.Language
import ir.dekot.fileto.feature_settings.domain.model.Theme
import ir.dekot.fileto.feature_settings.presentation.viewmodel.SettingsViewModel
import ir.dekot.fileto.R

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
                    ThemeRadioButton(
                        text = stringResource(id = language.displayNameRes),
                        selected = selectedLanguage == language,
                        onClick = { viewModel.onLanguageChange(language) }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeRadioButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}