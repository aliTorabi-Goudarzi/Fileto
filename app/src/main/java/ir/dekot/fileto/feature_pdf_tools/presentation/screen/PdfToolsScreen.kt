package ir.dekot.fileto.feature_pdf_tools.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import ir.dekot.fileto.feature_pdf_tools.presentation.components.ToolCard
import ir.dekot.fileto.feature_pdf_tools.presentation.viewmodel.PdfToolsViewModel

/**
 * صفحه اصلی ابزارهای PDF
 * این صفحه لیست تمام ابزارهای موجود را نمایش می‌دهد
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfToolsScreen(
    navController: NavController,
    viewModel: PdfToolsViewModel = hiltViewModel()
) {
    val availableTools by viewModel.availableTools.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(stringResource(id = R.string.pdf_tools)) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(id = R.string.back)
                        )
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
            Text(
                text = stringResource(id = R.string.pdf_tools_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(availableTools) { tool ->
                    ToolCard(
                        tool = tool,
                        onClick = {
                            // فعلاً فقط navigation را آماده می‌کنیم
                            // بعداً route های مربوطه را اضافه خواهیم کرد
                            navController.navigate(tool.route)
                        }
                    )
                }
            }
        }
    }
}
