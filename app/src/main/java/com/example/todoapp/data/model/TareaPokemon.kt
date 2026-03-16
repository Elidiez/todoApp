package com.example.todoapp.data.model

// --- CÓMO SE GUARDA LA TAREA EN FIREBASE ---
data class Tarea(
    val id: String = "",             // ID único de firebase
    val texto: String = "",          // "Entrenar a Squirtle"
    val fecha: String = "",          // "20/01/2026"
    val pokemonNombre: String? = null, // "squirtle"
    val imagenUrl: String? = null,   // URL de la foto
    val tipo: String? = null,        // "water"
    val hp: Int = 0,
    val ataque: Int = 0,
    val defensa: Int = 0,
    val velocidad: Int = 0
)

// --- CLASES PARA LEER LA RESPUESTA DE LA POKEAPI ---
// La API devuelve mucha basura, solo mapeamos lo que nos interesa

data class PokemonResponse(
    val name: String,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>
)

data class Sprites(
    val front_default: String? // La foto de frente
)

data class TypeSlot(
    val slot: Int,
    val type: TypeName
)

data class TypeName(
    val name: String // "fire", "water", etc.
)

data class StatSlot(
    val base_stat: Int,
    val stat: StatName
)

data class StatName(
    val name: String // "hp", "attack", etc.
)