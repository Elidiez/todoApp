package com.example.todoapp.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.todoapp.data.model.Tarea
import java.io.File
import java.io.FileOutputStream

fun exportarTareasTXT(context: Context, tareas: List<Tarea>) {
    val contenido = StringBuilder()

    contenido.append("MIS TAREAS POKÉMON\n")
    contenido.append("====================\n\n")

    tareas.forEach { t ->
        contenido.append("Tarea: ${t.texto}\n")
        contenido.append("Fecha: ${t.fecha}\n")
        if (t.pokemonNombre != null) {
            contenido.append("Pokémon: ${t.pokemonNombre.uppercase()}\n")
            contenido.append("Tipo: ${t.tipo}\n")
            contenido.append("HP: ${t.hp} - Ataque: ${t.ataque} - Defensa: ${t.defensa}\n")
        }
        contenido.append("----------------------------------\n")
    }

    try {
        val nombreArchivo = "tareas_pokemon_${System.currentTimeMillis()}.txt"

        // Lógica moderna para Android 10+ (Scoped Storage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(contenido.toString().toByteArray())
                }
                Toast.makeText(context, "Guardado en Descargas: $nombreArchivo", Toast.LENGTH_LONG).show()
            }
        } else {
            // Lógica antigua (para móviles viejos)
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, nombreArchivo)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(contenido.toString().toByteArray())
            }
            Toast.makeText(context, "Guardado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }

    } catch (e: Exception) {
        Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}