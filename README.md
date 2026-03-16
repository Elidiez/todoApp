# ToDoApp  +  PokeAPI

Una aplicación de gestión de tareas (ToDo List) desarrollada en **Android Studio** utilizando **Kotlin** y **Jetpack Compose**. El proyecto integra persistencia en la nube con Firebase y consumo de datos externos mediante la PokeAPI.

##  Características

- **Gestión de Tareas:** Crear, visualizar y eliminar tareas pendientes.
- **Sincronización en Tiempo Real:** Uso de **Firebase Firestore** para almacenar las tareas de forma que estén disponibles en cualquier dispositivo.
- **Integración con PokeAPI:** Consumo de servicios REST para mostrar datos de Pokémon dentro de la aplicación.
- **Interfaz Moderna:** Desarrollada íntegramente con **Jetpack Compose**, siguiendo las guías de Material Design 3.
- **Arquitectura MVVM:** Separación clara de responsabilidades (Model-View-ViewModel) para un código más limpio y mantenible.

##  Tecnologías utilizadas

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** Jetpack Compose
- **Base de Datos:** [Firebase Firestore](https://firebase.google.com/docs/firestore)
- **Networking:** Retrofit / Ktor (para la PokeAPI)
- **Inyección de Dependencias:** (Si usaste Hilt/Koin, añádelo aquí)
- **Gestor de dependencias:** Gradle (Kotlin DSL)

##  Estructura del Proyecto

```text
com.example.todoapp
├── data          # Modelos de datos y servicios de red (PokeAPI)
├── repository    # Gestión de Firestore y lógica de datos
├── ui            # Componentes, Screens y Temas (Compose)
├── viewmodel     # Lógica de negocio y estado de la UI
└── utils         # Clases de apoyo y utilidades
