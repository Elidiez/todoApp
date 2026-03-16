package com.example.todoapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.R

// Definimos el ID del canal aquí para que sea accesible globalmente
const val CHANNEL_ID = "inactivity_channel"

/**
 * Construye y muestra una notificación de inactividad.
 * @param context El contexto necesario para construir y mostrar la notificación.
 */
fun showInactivityNotification(context: Context) {
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.pokeball) // Asegúrate de tener este icono o cambia por R.drawable.ic_launcher_foreground
        .setContentTitle("Recordatorio")
        .setContentText("Hace rato que no añades ninguna tarea")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        // Comprueba si la app tiene permiso (necesario para Android 13+)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // El ID (1) sirve para actualizar o cancelar esta notificación específica luego si quisieras
            notify(1, builder.build())
        }
    }
}