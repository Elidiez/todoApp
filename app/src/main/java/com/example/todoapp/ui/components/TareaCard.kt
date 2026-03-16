package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.todoapp.data.model.Tarea

@Composable
fun TareaCard(tarea: Tarea, colorTexto: Color, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Título y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // AQUI USAMOS TU COLOR SELECCIONADO
                    Text(
                        text = tarea.texto,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorTexto // <--- CAMBIO AQUÍ (Antes era Color.Black)
                    )
                    if (tarea.fecha.isNotEmpty()) {
                        Text(text = "📅 ${tarea.fecha}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                }
            }

            // 2. Si detectamos un Pokémon, mostramos su ficha
            if (tarea.pokemonNombre != null) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Imagen del Pokémon (Usando Coil)
                    AsyncImage(
                        model = tarea.imagenUrl,
                        contentDescription = tarea.pokemonNombre,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Black, shape = RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Estadísticas
                    Column {
                        Text(
                            text = tarea.pokemonNombre.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = "Tipo: ${tarea.tipo}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "❤️ HP: ${tarea.hp}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "⚔️ Atk: ${tarea.ataque}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "🛡️ Def: ${tarea.defensa}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "⚡ Vel: ${tarea.velocidad}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}