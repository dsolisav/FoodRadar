package com.example.foodradar.logic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodradar.R
import com.example.foodradar.data.Usuario
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Sesion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioSesion : AppCompatActivity() {

    lateinit var contrasena : EditText
    lateinit var email : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_sesion)

        //Copiar archivo json desde assets a internal storage (si es necesario)
        Funciones.copyJsonToInternalStorageIfNeededUsers(this)
        Funciones.copyJsonToInternalStorageIfNeededComments(this)


        contrasena = findViewById<EditText>(R.id.Contraseña)
        email = findViewById<EditText>(R.id.Usuario)
        val TextView = findViewById<TextView>(R.id.CrearCuenta)
        val BotonIniciarSesion = findViewById<Button>(R.id.BotonIngreso)


        TextView.setOnClickListener {
            val intent = Intent(this, CreacionCuenta::class.java)
            startActivity(intent)
        }

        BotonIniciarSesion.setOnClickListener {
            botonIniciarSesion()
        }

    }

    fun botonIniciarSesion(){
        val email = email.text.toString()
        val contrasena = contrasena.text.toString()

        if (email.isNotEmpty() && contrasena.isNotEmpty()) {
            validarUsuario(email, contrasena)
        } else{
            Toast.makeText(this, "Ingrese los campos para continuar", Toast.LENGTH_SHORT).show()
        }

    }

    fun validarUsuario(email: String, contrasena: String){
        Sesion.auth.signInWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("AUTH-LOGIN", "signInWithEmail:success")
                    val user = Sesion.auth.currentUser
                    Toast.makeText(this, "¡Bienvenido de vuelta!", Toast.LENGTH_SHORT).show()
                    setSesion(email)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("AUTH-LOGIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Usuario no encontrado, verifica tus credenciales.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun setSesion(email: String){

        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("usuarios")
        collectionRef
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Check if any documents were found
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        // Convert the document to your data class
                        Sesion.userId = document.id as? String ?: ""
                        val doc = document.data
                        Log.i("GET-USER","Found document: $doc")
                        Sesion.userName = doc?.get("userName") as? String ?: ""
                        Sesion.nombre = doc?.get("nombre") as? String ?: ""
                        Sesion.apellido = doc?.get("apellido") as? String ?: ""
                        Sesion.email = doc?.get("email") as? String ?: ""
                        val restaurantePath = doc?.get("restaurante") as? String
                        if (restaurantePath != null) {
                            val restauranteRef = db.document(restaurantePath)
                            restauranteRef.get()
                                .addOnSuccessListener { restauranteDocument ->
                                    if (restauranteDocument.exists()) {
                                        val restauranteData = restauranteDocument.data
                                        // Access specific fields if needed, for example:
                                        val nombre = restauranteData?.get("nombre") as? String
                                        if (!nombre.isNullOrEmpty())  {
                                            Sesion.restaurante["restaurantId"] = restauranteDocument.id
                                            Sesion.esRestaurante=true
                                            Log.i("GET-RESTAURANTE","Nombre del restaurante: $nombre")
                                            Sesion.restaurante["nombre"] = nombre
                                            Sesion.restaurante["categoria"] = restauranteData["categoria"] as? String ?: ""
                                            Sesion.restaurante["calificacion"] = restauranteData["calificacion"] as? Double ?: 0.0
                                            Sesion.restaurante["longitud"] = restauranteData["longitud"] as? Double ?: 0.0
                                            Sesion.restaurante["latitud"] = restauranteData["latitud"] as? Double ?: 0.0
                                            Sesion.restaurante["visibilidad"] = restauranteData["visibilidad"] as? Boolean ?: false
                                            Log.i("GET-RESTAURANTE", Sesion.restaurante.toString())
                                            Log.i("GET-SESION", Sesion.userId)
                                            Log.i("GET-SESION",
                                                Sesion.restaurante["restaurantId"].toString()
                                            )
                                        }
                                        val intent = Intent(this, Opciones::class.java)
                                        startActivity(intent)

                                    } else {
                                        Log.w("GET-RESTAURANTE", "No such document!")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.w("GET-RESTAURANTE", "Error getting document", e)
                                }
                        }



                    }
                } else {
                    Log.i("GET-USER","No document found with the specified title.")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                Toast.makeText(this, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
            }
    }
}
