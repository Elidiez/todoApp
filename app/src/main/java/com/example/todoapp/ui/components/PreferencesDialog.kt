package com.example.todoapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreferencesDialog(
    darkMode: Boolean,
    strangerThingsMode: Boolean,
    selectedColor: String,
    onDismiss: () -> Unit,
    onDarkModeChanged: (Boolean) -> Unit,
    onStrangerThingsModeChanged: (Boolean) -> Unit,
    onColorSelected: (String) -> Unit
) {
    val colores = listOf("Rojo", "Verde", "Azul")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Preferencias") },
        text = {
            Column {
                // Selección de Color
                colores.forEach { color ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = (selectedColor == color),
                            onClick = { onColorSelected(color) }
                        )
                        Text(color)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Switch Dark Mode
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dark Mode", modifier = Modifier.weight(1f))
                    Switch(
                        checked = darkMode,
                        onCheckedChange = onDarkModeChanged
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Switch Stranger Things Mode
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Stranger Things Mode", modifier = Modifier.weight(1f))
                    Switch(
                        checked = strangerThingsMode,
                        onCheckedChange = onStrangerThingsModeChanged
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}