package ir.dekot.fileto.feature_pdf_tools.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ir.dekot.fileto.R
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitType

/**
 * کامپوننت کارت تنظیمات تقسیم PDF
 */
@Composable
fun SplitOptionsCard(
    selectedSplitType: SplitType,
    pageRangeInput: String,
    pagesPerSplitInput: String,
    onSplitTypeChanged: (SplitType) -> Unit,
    onPageRangeChanged: (String) -> Unit,
    onPagesPerSplitChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.select_split_type),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // گزینه: هر صفحه جداگانه
            SplitTypeOption(
                splitType = SplitType.EACH_PAGE,
                title = stringResource(id = R.string.split_each_page),
                description = "هر صفحه در فایل جداگانه ذخیره می‌شود",
                isSelected = selectedSplitType == SplitType.EACH_PAGE,
                onSelected = { onSplitTypeChanged(SplitType.EACH_PAGE) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // گزینه: تقسیم بر اساس تعداد صفحات
            SplitTypeOption(
                splitType = SplitType.BY_PAGES,
                title = stringResource(id = R.string.split_by_pages),
                description = "تقسیم بر اساس تعداد صفحات مشخص",
                isSelected = selectedSplitType == SplitType.BY_PAGES,
                onSelected = { onSplitTypeChanged(SplitType.BY_PAGES) }
            )
            
            // فیلد ورودی برای تعداد صفحات
            if (selectedSplitType == SplitType.BY_PAGES) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pagesPerSplitInput,
                    onValueChange = onPagesPerSplitChanged,
                    label = { Text(stringResource(id = R.string.pages_per_split)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // گزینه: تقسیم بر اساس بازه
            SplitTypeOption(
                splitType = SplitType.BY_RANGE,
                title = stringResource(id = R.string.split_by_range),
                description = "تقسیم بر اساس بازه صفحات مشخص",
                isSelected = selectedSplitType == SplitType.BY_RANGE,
                onSelected = { onSplitTypeChanged(SplitType.BY_RANGE) }
            )
            
            // فیلد ورودی برای بازه صفحات
            if (selectedSplitType == SplitType.BY_RANGE) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pageRangeInput,
                    onValueChange = onPageRangeChanged,
                    label = { Text(stringResource(id = R.string.page_range)) },
                    placeholder = { Text("مثال: 1-5, 7, 9-12") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

/**
 * کامپوننت گزینه نوع تقسیم
 */
@Composable
private fun SplitTypeOption(
    splitType: SplitType,
    title: String,
    description: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                role = Role.RadioButton
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null // onClick is handled by the Row
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
