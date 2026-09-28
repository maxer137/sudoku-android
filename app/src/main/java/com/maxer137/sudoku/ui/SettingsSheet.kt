package com.maxer137.sudoku.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.maxer137.sudoku.data.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: Settings,
    onSettingsChange: (Settings) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text("Settings", style = MaterialTheme.typography.titleLarge)
            SettingSwitch(
                label = "Highlight row and column",
                checked = settings.highlightRowAndColumn,
                onCheckedChange = { onSettingsChange(settings.copy(highlightRowAndColumn = it)) },
            )
            SettingSwitch(
                label = "Highlight identical numbers",
                checked = settings.highlightSameDigits,
                onCheckedChange = { onSettingsChange(settings.copy(highlightSameDigits = it)) },
            )
            SettingSwitch(
                label = "Show conflicting numbers",
                checked = settings.showConflicts,
                onCheckedChange = { onSettingsChange(settings.copy(showConflicts = it)) },
            )
            SettingSwitch(
                label = "Show timer",
                checked = settings.showTimer,
                onCheckedChange = { onSettingsChange(settings.copy(showTimer = it)) },
            )
            SettingSwitch(
                label = "Number pad on the left on wide screens",
                checked = settings.numberPadOnLeft,
                onCheckedChange = { onSettingsChange(settings.copy(numberPadOnLeft = it)) },
            )
        }
    }
}

@Composable
private fun SettingSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = null)
    }
}
