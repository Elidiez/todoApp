package com.example.todoapp.data.network

import com.example.todoapp.data.model.PokemonResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// 1. La Interfaz: Define CÓMO pedimos los datos
// Es como la ventanilla de información: "Deme los datos del pokemon X"
interface PokeApiService {
    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): PokemonResponse
}

// 2. El Objeto Cliente: Configura la conexión
// Es como configurar el navegador web (la URL base, el convertidor de JSON, etc.)
object RetrofitClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    val service: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte el JSON raro en nuestras clases Kotlin
            .build()
            .create(PokeApiService::class.java)
    }
}