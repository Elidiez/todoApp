package com.example.todoapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.data.model.Tarea
import com.example.todoapp.ui.components.MyTopAppBar
import com.example.todoapp.ui.components.PreferencesDialog
import com.example.todoapp.ui.components.TareaCard
import com.example.todoapp.ui.viewmodel.TareasViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

// --- IMPORTS CORREGIDOS ---
// Ahora apuntan a la carpeta "utils" donde creaste los archivos
import com.example.todoapp.utils.convertMillisToDate
import com.example.todoapp.utils.exportarTareasTXT
import com.example.todoapp.utils.showInactivityNotification
// --------------------------

// Umbral para el sensor de sacudida
private const val SHAKE_THRESHOLD = 800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadScreen(
    nombre: String,
    alias: String,
    darkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onLogout: () -> Unit,
    viewModel: TareasViewModel = viewModel()
) {
    // ----------------------------------------------------------------
    // 1. ESTADOS Y DATOS (MVVM)
    // ----------------------------------------------------------------

    val lista by viewModel.listaTareas.collectAsState(initial = emptyList())

    // Estados de UI locales
    var activity by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("Rojo") }

    // Estados de Diálogos
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Tarea?>(null) }
    var showPreferencesDialog by remember { mutableStateOf(false) }

    // Estados DatePicker
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()

    // Estados Sensores
    var strangerThingsMode by remember { mutableStateOf(false) }
    var isUpsideDown by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // ----------------------------------------------------------------
    // 2. LÓGICA DE SENSORES (Stranger Things)
    // ----------------------------------------------------------------
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    val shakeListener = remember {
        object : SensorEventListener {
            private var lastUpdate: Long = 0
            private var lastX = 0f; private var lastY = 0f; private var lastZ = 0f

            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
            override fun onSensorChanged(e: SensorEvent?) {
                if (e?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val curTime = System.currentTimeMillis()
                    if ((curTime - lastUpdate) > 100) {
                        val diff = curTime - lastUpdate
                        lastUpdate = curTime
                        val x = e.values[0]; val y = e.values[1]; val z = e.values[2]
                        val speed = abs(x + y + z - lastX - lastY - lastZ) / diff * 10000

                        if (speed > SHAKE_THRESHOLD) isUpsideDown = !isUpsideDown

                        lastX = x; lastY = y; lastZ = z
                    }
                }
            }
        }
    }

    DisposableEffect(strangerThingsMode) {
        if (strangerThingsMode) {
            sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        } else {
            isUpsideDown = false
        }
        onDispose { sensorManager.unregisterListener(shakeListener) }
    }

    // ----------------------------------------------------------------
    // 3. PERMISOS Y NOTIFICACIONES
    // ----------------------------------------------------------------
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        } else mutableStateOf(true)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasNotificationPermission = it }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Timer de inactividad
    // AHORA FUNCIONARÁ PORQUE YA HEMOS IMPORTADO LA FUNCIÓN CORRECTAMENTE ARRIBA
    LaunchedEffect(lista.size) {
        if (hasNotificationPermission && lista.isNotEmpty()) {
            delay(10000) // 10 segundos
            showInactivityNotification(context)
        }
    }

    // ----------------------------------------------------------------
    // 4. INTERFAZ DE USUARIO (SCAFFOLD)
    // ----------------------------------------------------------------
    Scaffold(
        topBar = {
            MyTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
                onPreferencesClick = { showPreferencesDialog = true },
                onLogoutClick = onLogout
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { exportarTareasTXT(context, lista) },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Text("TXT", modifier = Modifier.padding(8.dp), color = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .graphicsLayer { rotationZ = if (isUpsideDown && strangerThingsMode) 180f else 0f },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Cabecera
            Text("Hola, $alias")
            Text(text = nombre, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(16.dp))

            // --- FILA DE ENTRADA ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = activity,
                    onValueChange = { activity = it },
                    label = {
                        if (selectedDateText.isNotEmpty()) Text("Para el $selectedDateText")
                        else Text("Ej: Entrenar a Pikachu")
                    },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Fecha",
                        tint = if (selectedDateText.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                Button(
                    modifier = Modifier.wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta),
                    onClick = {
                        if (activity.isNotBlank()) {
                            val fechaFinal = if (selectedDateText.isNotEmpty()) {
                                selectedDateText
                            } else {
                                convertMillisToDate(System.currentTimeMillis())
                            }

                            viewModel.agregarTarea(activity, fechaFinal)

                            activity = ""
                            selectedDateText = ""
                        }
                    }
                ) { Text("Añadir") }
            }

            // --- LISTA ---
            val filteredList = lista.filter {
                it.texto.contains(searchQuery, ignoreCase = true)
            }

            val colorReal = when (selectedColor) {
                "Rojo" -> Color.Red
                "Verde" -> Color(0xFF00C853)
                "Azul" -> Color.Blue
                else -> Color.Black
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(filteredList) { tarea ->
                    TareaCard(
                        tarea = tarea,
                        colorTexto = colorReal,
                        onDelete = {
                            itemToDelete = tarea
                            showDeleteDialog = true
                        }
                    )
                }
            }

            // --- DIÁLOGOS ---
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDateText = convertMillisToDate(it)
                            }
                            showDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                    }
                ) { DatePicker(state = datePickerState) }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Eliminar \"${itemToDelete?.texto}\"?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                itemToDelete?.let { viewModel.borrarTarea(it.id) }
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) { Text("Eliminar") }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                    }
                )
            }

            if (showPreferencesDialog) {
                PreferencesDialog(
                    darkMode = darkMode,
                    strangerThingsMode = strangerThingsMode,
                    selectedColor = selectedColor,
                    onDismiss = { showPreferencesDialog = false },
                    onDarkModeChanged = onDarkModeChanged,
                    onStrangerThingsModeChanged = { strangerThingsMode = it },
                    onColorSelected = { selectedColor = it }
                )
            }
        }
    }
}