package com.example.todoapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Función de utilidad para convertir milisegundos a texto legible.
 * Ejemplo: 16788888 -> "20/01/2026"
 */
fun convertMillisToDate(millis: Long): String {
    // Usamos Locale.getDefault() para que se adapte al idioma del móvil del usuario,
    // o puedes forzar Locale("es", "ES") si prefieres.
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}