package com.example.foodradar.data


import android.content.Context
import android.location.Location
import android.util.Log
import com.example.foodradar.data.Data.Companion.RADIUS_OF_EARTH_KM
import com.example.foodradar.logic.Mapa
import com.example.foodradar.logic.Paradas
import com.example.foodradar.logic.RestaurantsAdapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.json.JSONObject
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import kotlin.math.roundToInt
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class Funciones {
    companion object {
        fun escucharRestaurantes(listener: RestaurantesListener, categoriaSeleccionada: String) {

            val db = Firebase.firestore
            val collectionRef = db.collection("restaurantes")
            val query = collectionRef
                .whereEqualTo("categoria", categoriaSeleccionada)
            query.addSnapshotListener{ snapshot, error->
                if(error != null){
                    Log.e("FirestoreQuery","Error: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null &&snapshot.documents.isNotEmpty()){
                    val newRestaurantList = mutableListOf<Restaurant>()
                    for (document in snapshot.documents) {
                        val doc = document.data
                        val id = document.id
                        val calificacion = doc?.get("calificacion") as? Double ?: 0.0
                        val categoria = doc?.get("categoria") as? String ?: ""
                        val latitud = doc?.get("latitud") as? Double ?: 0.0
                        val longitud = doc?.get("longitud") as? Double ?: 0.0
                        val nombre = doc?.get("nombre") as? String ?: ""
                        val visibilidad = doc?.get("visibilidad") as? Boolean ?: false

                        // Verifica si el restaurante cumple con las condiciones
                        val distancia =
                            Data.latitud?.let {
                                Data.longitud?.let { it1 ->
                                    distance(
                                        it,
                                        it1, latitud, longitud
                                    )
                                }
                            }
                        if (distancia != null) {
                            if (visibilidad) {
                                val restaurante = Restaurant(
                                    id,
                                    nombre,
                                    categoria,
                                    calificacion,
                                    longitud,
                                    latitud
                                )
                                newRestaurantList.add(restaurante)
                                Log.i("FirestoreQuery", "Added restaurant: $doc")
                            } else {
                                Log.i("FirestoreQuery", "Skipped restaurant: $doc")
                            }
                        }
                    }
                    Data.RESTAURANT_LIST.clear()
                    Data.RESTAURANT_LIST.addAll(newRestaurantList)
                    listener.onRestaurantesActualizados(newRestaurantList)
                } else{
                    Log.d("FirestoreQuery","Esperando Datos...")
                }
            }
        }

        fun loadJSONFromAsset(context: Context): String? {
            return try {
                val isStream: InputStream = context.assets.open("restaurantes.json")
                val size: Int = isStream.available()
                val buffer = ByteArray(size)
                isStream.read(buffer)
                isStream.close()
                String(buffer, Charsets.UTF_8)
            } catch (ex: IOException) {
                Log.e("Funciones", "Error leyendo el archivo destinos.json: ${ex.message}")
                null
            }
        }

        //Copiar archivo desde assets a internal storage para poder actualizarlo
        fun copyJsonToInternalStorageIfNeededUsers(context: Context) {
            if (!isFileCopiedUsers(context)) {
                try {
                    val inputStream = context.assets.open("usuarios.json")
                    val outputStream = context.openFileOutput("usuarios.json", Context.MODE_PRIVATE)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()

                    // Set the flag after successful copying
                    setFileCopiedUsers(context)

                } catch (e: IOException) {
                    Log.e("Funciones", "Error copying file: ${e.message}")
                }
            } else {
                Log.d("Funciones", "File already copied, skipping...")
            }
        }

        fun copyJsonToInternalStorageIfNeededComments(context: Context) {
            if (!isFileCopiedComments(context)) {
                try {
                    val inputStream = context.assets.open("comentarios.json")
                    val outputStream = context.openFileOutput("comentarios.json", Context.MODE_PRIVATE)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()

                    // Set the flag after successful copying
                    setFileCopiedComments(context)

                } catch (e: IOException) {
                    Log.e("Funciones", "Error copying file: ${e.message}")
                }
            } else {
                Log.d("Funciones", "File already copied, skipping...")
            }
        }

        //Cargar archivo desde internal storage
        fun loadUsersJSONFromInternalStorage(context: Context): String? {
            return try {
                val inputStream: InputStream = context.openFileInput("usuarios.json")
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charsets.UTF_8)
            } catch (ex: IOException) {
                Log.e("Funciones", "Error reading the file: ${ex.message}")
                null
            }
        }

        fun loadCommentsJSONFromInternalStorage(context: Context): String? {
            return try {
                val inputStream: InputStream = context.openFileInput("comentarios.json")
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charsets.UTF_8)
            } catch (ex: IOException) {
                Log.e("Funciones", "Error reading the file: ${ex.message}")
                null
            }
        }

        //Agregar nuevo usuario al json
        fun addNewUserToUsuarios(context: Context, newUser: JSONObject) {
            try {
                // Load the existing JSON file
                val usersJSON = loadUsersJSONFromInternalStorage(context)

                if (usersJSON != null) {
                    // Convert the loaded JSON string to a JSONObject
                    val jsonObject = JSONObject(usersJSON)
                    val usuariosArray = jsonObject.getJSONArray("usuarios")

                    // Add the new user to the usuarios array
                    usuariosArray.put(newUser)

                    // Write the updated JSON back to internal storage
                    val updatedJSON = jsonObject.toString()
                    val outputStream: FileOutputStream = context.openFileOutput("usuarios.json", Context.MODE_PRIVATE)
                    outputStream.write(updatedJSON.toByteArray())
                    outputStream.close()

                    Log.d("Funciones", "User added successfully.")
                } else {
                    Log.e("Funciones", "Failed to load existing JSON file.")
                }
            } catch (ex: Exception) {
                Log.e("Funciones", "Error updating JSON file: ${ex.message}")
            }
        }

        fun addNewCommentToComentarios(context: Context, newComment: JSONObject) {
            try {
                // Load the existing JSON file
                val commentsJSON = loadCommentsJSONFromInternalStorage(context)

                if (commentsJSON != null) {
                    // Convert the loaded JSON string to a JSONObject
                    val jsonObject = JSONObject(commentsJSON)
                    val commentsArray = jsonObject.getJSONArray("comentarios")

                    // Create a new JSONArray to store the new order of comments
                    val updatedCommentsArray = JSONArray()

                    // Add the new comment to the start of the array
                    updatedCommentsArray.put(newComment)

                    // Add the existing comments after the new comment
                    for (i in 0 until commentsArray.length()) {
                        updatedCommentsArray.put(commentsArray.get(i))
                    }

                    // Update the original JSONObject with the new array
                    jsonObject.put("comentarios", updatedCommentsArray)

                    // Write the updated JSON back to internal storage
                    val updatedJSON = jsonObject.toString()
                    val outputStream: FileOutputStream = context.openFileOutput("comentarios.json", Context.MODE_PRIVATE)
                    outputStream.write(updatedJSON.toByteArray())
                    outputStream.close()

                    Log.d("Funciones", "Comment added successfully at the start.")
                } else {
                    Log.e("Funciones", "Failed to load existing JSON file.")
                }
            } catch (ex: Exception) {
                Log.e("Funciones", "Error updating JSON file: ${ex.message}")
            }
        }

        //Obtener usuario por username
        fun getUserByUsername(context: Context, username: String): Usuario? {
            val jsonString = loadUsersJSONFromInternalStorage(context) ?: return null
            val gson = Gson()
            val usuarios = gson.fromJson(jsonString, Usuarios::class.java)
            return usuarios.usuarios.find { it.userName == username }
        }

        //Crear nuevo usuario
        fun createNewUser(usuario : Usuario): JSONObject {
            return JSONObject().apply {
                put("id", usuario.id)
                put("userName", usuario.userName)
                put("nombre", usuario.nombre)
                put("apellido", usuario.apellido)
                put("email", usuario.email)
                put("restaurante", JSONObject().apply {
                    put("nombre", usuario.restaurante.nombre)
                    put("categoria", usuario.restaurante.categoria)
                    put("calificacion", 0)
                    put("longitud", usuario.restaurante.longitud)
                    put("latitud", usuario.restaurante.latitud)
                })
            }
        }

        // TO-DO ENTREGA FINAL
        // fun createNewComment(comentario : Comentario): JSONObject {
        //     return JSONObject().apply {
        //         put("nombre_completo", comentario.nombre_completo)
        //         put("calificacion", comentario.calificacion)
        //         put("fecha", comentario.fecha)
        //         put("descripcion", comentario.descripcion)

        //     }
        // }

        //Verificar si el archivo ya fue copiado
        fun isFileCopiedUsers(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            return sharedPref.getBoolean("isFileCopiedUsers", false)
        }

        //Establecer que el archivo ya fue copiado
        fun setFileCopiedUsers(context: Context) {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("isFileCopiedUsers", true)
                apply() // Save the flag as true
            }
        }

        fun isFileCopiedComments(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            return sharedPref.getBoolean("isFileCopiedComments", false)
        }

        //Establecer que el archivo ya fue copiado
        fun setFileCopiedComments(context: Context) {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("isFileCopiedComments", true)
                apply() // Save the flag as true
            }
        }

        //Editar/actualizar usuario
        fun editUserInUsuarios(context: Context, username: String, newNombre: String, newApellido: String, newEmail: String, newUserName: String) {
            try {
                // Load the existing JSON file
                val usersJSON = loadUsersJSONFromInternalStorage(context)

                if (usersJSON != null) {
                    // Convert the loaded JSON string to a JSONObject
                    val jsonObject = JSONObject(usersJSON)
                    val usuariosArray = jsonObject.getJSONArray("usuarios")

                    // Find the user by their username and update their fields
                    for (i in 0 until usuariosArray.length()) {
                        val user = usuariosArray.getJSONObject(i)
                        if (user.getString("userName") == username) {
                            user.put("nombre", newNombre)
                            user.put("apellido", newApellido)
                            user.put("email", newEmail)
                            user.put("userName", newUserName)
                            break
                        }
                    }

                    // Write the updated JSON back to internal storage
                    val updatedJSON = jsonObject.toString()
                    val outputStream: FileOutputStream = context.openFileOutput("usuarios.json", Context.MODE_PRIVATE)
                    outputStream.write(updatedJSON.toByteArray())
                    outputStream.close()

                    Log.d("Funciones", "User updated successfully.")
                } else {
                    Log.e("Funciones", "Failed to load existing JSON file.")
                }
            } catch (ex: Exception) {
                Log.e("Funciones", "Error updating JSON file: ${ex.message}")
            }
        }

        fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
            val latDistance = Math.toRadians(lat1 - lat2)
            val lngDistance = Math.toRadians(long1 - long2)
            val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2))
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val result = RADIUS_OF_EARTH_KM * c
            return (result * 100.0).roundToInt() / 100.0

        }

        fun clearSesion(){
            Sesion.auth.signOut()
            Sesion.userId = ""
            Sesion.userName = ""
            Sesion.nombre = ""
            Sesion.apellido = ""
            Sesion.email = ""
            Sesion.esRestaurante = false
            Sesion.restaurante = mutableMapOf(
                "restaurantId" to "",
                "nombre" to "",
                "categoria" to "",
                "calificacion" to 0.0,
                "longitud" to 0.0,
                "latitud" to 0.0,
                "visibilidad" to false
            )
        }
    }
}

