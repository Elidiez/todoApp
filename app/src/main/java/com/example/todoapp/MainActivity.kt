package com.example.todoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.theme.TodoappTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// --- IMPORTACIONES DE TUS NUEVAS CARPETAS ---
// 1. Utils (Donde pusiste el ID del canal)
import com.example.todoapp.utils.CHANNEL_ID

// 2. Repository (Donde gestionas el DataStore/Preferencias)
// Si tu archivo se llama diferente, ajusta este import y la variable dentro de setContent
import com.example.todoapp.data.repository.SettingsRepository

// 3. Screens (Tus pantallas, ahora ordenadas)
import com.example.todoapp.ui.screens.ActividadScreen
import com.example.todoapp.ui.screens.AuthScreens // Si mantuviste el object, o importa las funciones sueltas si las sacaste

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Crear canal de notificaciones (requerido para Android 8+)
        createNotificationChannel()

        setContent {
            // 2. Inicializar DataStore (Preferencias)
            val context = LocalContext.current
            // Asegúrate de que esta clase coincida con la que creaste en data/repository
            val settingsRepository = remember { SettingsRepository(context) }

            // 3. Observar el estado del modo oscuro
            val darkMode by settingsRepository.darkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            // 4. Aplicar el tema
            TodoappTheme(darkTheme = darkMode) {
                // 5. Iniciar la Navegación
                AppNav(
                    darkMode = darkMode,
                    onDarkModeChanged = { newDarkMode ->
                        scope.launch {
                            settingsRepository.setDarkMode(newDarkMode)
                        }
                    }
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Canal de Inactividad"
            val descriptionText = "Notificaciones por inactividad del usuario"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // Usamos la constante CHANNEL_ID que está en tu archivo utils
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

/**
 * Gestión de la navegación.
 * Puede ir aquí mismo o en un archivo ui/AppNavigation.kt si prefieres.
 */
@Composable
fun AppNav(darkMode: Boolean, onDarkModeChanged: (Boolean) -> Unit) {
    val nav = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // Lógica de Autologin
    val startDestination = if (auth.currentUser != null) "actividad" else "login"

    NavHost(navController = nav, startDestination = startDestination) {

        // PANTALLA DE LOGIN
        composable("login") {
            // Si sacaste las funciones del object AuthScreens, borra "AuthScreens."
            AuthScreens.LoginScreen(
                onLoginSuccess = {
                    nav.navigate("actividad") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    nav.navigate("register")
                }
            )
        }

        // PANTALLA DE REGISTRO
        composable("register") {
            AuthScreens.RegisterScreen(
                onRegisterSuccess = {
                    nav.navigate("actividad") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    nav.popBackStack()
                }
            )
        }

        // PANTALLA PRINCIPAL (ACTIVIDAD)
        composable("actividad") {
            val currentUser = auth.currentUser
            val nombre = currentUser?.email ?: "Usuario"
            val alias = if (nombre.contains("@")) nombre.split("@")[0] else "Alias"

            // Aquí llamamos a tu nueva ActividadScreen limpia
            ActividadScreen(
                nombre = nombre,
                alias = alias,
                darkMode = darkMode,
                onDarkModeChanged = onDarkModeChanged,
                onLogout = {
                    auth.signOut()
                    nav.navigate("login") {
                        popUpTo("actividad") { inclusive = true }
                    }
                }
                // No hace falta pasar el ViewModel, ActividadScreen lo crea internamente
            )
        }
    }
}