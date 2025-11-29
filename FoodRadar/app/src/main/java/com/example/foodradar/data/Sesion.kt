package com.example.foodradar.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class Sesion {
    companion object {
        var userId = ""
        var restaurantMode = ""
        var userName = ""
        var nombre = ""
        var apellido = ""
        var email = ""
        var esRestaurante = false
        var restaurante: MutableMap<String, Any> = mutableMapOf(
            "restaurantId" to "",
            "nombre" to "",
            "categoria" to "",
            "calificacion" to 0.0,
            "longitud" to 0.0,
            "latitud" to 0.0,
            "visibilidad" to false
        )
        var auth = Firebase.auth
        var imagesRef = Firebase.storage.reference.child("images")
    }
}
