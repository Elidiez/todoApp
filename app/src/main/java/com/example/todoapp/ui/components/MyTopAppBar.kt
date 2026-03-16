package com.example.todoapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onPreferencesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    // Estado para controlar si el menú desplegable está abierto o cerrado.
    var expanded by remember { mutableStateOf(false) }
    // Estado para controlar si la barra de búsqueda está visible.
    var searchVisible by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            // Muestra un campo de texto si la búsqueda está activa, o el título normal si no.
            if (searchVisible) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Buscar tarea...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            } else {
                Text("Mis Proyectos")
            }
        },
        navigationIcon = {
            // Icono de menú a la izquierda.
            Box { // Box se usa para anclar el DropdownMenu al IconButton.
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú")
                }
                // Menú que aparece al pulsar el icono.
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false } // Se cierra si se pulsa fuera.
                ) {
                    DropdownMenuItem(
                        text = { Text("Preferencias") },
                        onClick = {
                            onPreferencesClick()
                            expanded = false
                        }
                    )
                    // Opción de Logout en el menú
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión", color = Color.Red) },
                        onClick = {
                            expanded = false
                            onLogoutClick()
                        }
                    )
                }
            }
        },
        actions = {
            // Iconos de acción a la derecha.
            if (searchVisible) {
                // Si la búsqueda está activa, muestra un icono para cerrarla.
                IconButton(onClick = {
                    searchVisible = false
                    onSearchQueryChanged("") // Limpia el texto de búsqueda al cerrar.
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar búsqueda")
                }
            } else {
                // Si no, muestra el icono de búsqueda.
                IconButton(onClick = { searchVisible = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}