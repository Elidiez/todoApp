package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.model.Tarea
import com.example.todoapp.data.network.RetrofitClient // O como lo hayas renombrado
import com.example.todoapp.data.repository.FirestoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TareasViewModel : ViewModel() {

    // Estado de la lista (Observable como en tu imagen de StateFlow)
    // Usamos el Flow que ya creaste en FirestoreManager
    val listaTareas = FirestoreManager.getTareasFlow()

    // Lógica para añadir tarea (MovidA desde Actividad)
    fun agregarTarea(texto: String, fecha: String) {
        viewModelScope.launch {
            val palabras = texto.trim().split(" ")
            val posiblePokemon = palabras.last().lowercase()

            val nuevaTarea = try {
                // Intento de llamada a API
                val respuesta = RetrofitClient.service.getPokemon(posiblePokemon)
                Tarea(
                    texto = texto,
                    fecha = fecha,
                    pokemonNombre = respuesta.name,
                    imagenUrl = respuesta.sprites.front_default,
                    tipo = respuesta.types.joinToString(", ") { it.type.name },
                    hp = respuesta.stats.find { it.stat.name == "hp" }?.base_stat ?: 0,
                    ataque = respuesta.stats.find { it.stat.name == "attack" }?.base_stat ?: 0,
                    defensa = respuesta.stats.find { it.stat.name == "defense" }?.base_stat ?: 0,
                    velocidad = respuesta.stats.find { it.stat.name == "speed" }?.base_stat ?: 0
                )
            } catch (e: Exception) {
                // Fallo o no es pokemon
                Tarea(texto = texto, fecha = fecha)
            }

            // Guardar en Firebase
            FirestoreManager.addTarea(nuevaTarea)
        }
    }

    fun borrarTarea(id: String) {
        viewModelScope.launch {
            FirestoreManager.deleteTarea(id)
        }
    }
}