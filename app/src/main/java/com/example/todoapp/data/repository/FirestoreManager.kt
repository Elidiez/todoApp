package com.example.todoapp.data.repository

import com.example.todoapp.data.model.Tarea
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Referencia a la colección del usuario actual (cada usuario tiene sus propias tareas)
    private fun getCollectionReference() =
        auth.currentUser?.email?.let { email ->
            db.collection("usuarios").document(email).collection("tareas")
        }

    // 1. GUARDAR TAREA
    fun addTarea(tarea: Tarea) {
        val ref = getCollectionReference() ?: return
        // Creamos un ID único automático
        val id = ref.document().id
        val tareaConId = tarea.copy(id = id)
        ref.document(id).set(tareaConId)
    }

    // 2. ESCUCHAR TAREAS EN TIEMPO REAL (Flow)
    fun getTareasFlow(): Flow<List<Tarea>> = callbackFlow {
        val ref = getCollectionReference()

        if (ref == null) {
            close()
            return@callbackFlow
        }

        // Nos suscribimos a cambios
        val subscription = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                // Convertimos los documentos de Firebase a objetos Tarea
                val tareas = snapshot.documents.mapNotNull { it.toObject<Tarea>() }
                trySend(tareas)
            }
        }

        // Cuando dejamos de escuchar, cerramos la conexión
        awaitClose { subscription.remove() }
    }

    // 3. BORRAR TAREA
    fun deleteTarea(tareaId: String) {
        getCollectionReference()?.document(tareaId)?.delete()
    }
}