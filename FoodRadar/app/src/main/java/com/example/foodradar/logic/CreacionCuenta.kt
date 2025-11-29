package com.example.foodradar.logic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodradar.R
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Restaurante
import com.example.foodradar.data.Sesion
import com.example.foodradar.data.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlin.random.Random

class
CreacionCuenta: AppCompatActivity() {
    lateinit var botonSoyRestaurante: Button
    lateinit var textIniciarSesion : TextView
    lateinit var botonCrearCuenta : Button
    lateinit var nombre : EditText
    lateinit var apellido : EditText
    lateinit var correo : EditText
    lateinit var usuario : EditText
    lateinit var contrasena : EditText
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*enableEdgeToEdge()*/
        setContentView(R.layout.creacion_cuenta)

        //Inicializacion de vistas
        textIniciarSesion = findViewById<TextView>(R.id.InicioSesion)
        botonCrearCuenta = findViewById<Button>(R.id.BotonCrearCuenta)
        nombre = findViewById<EditText>(R.id.Nombre)
        apellido = findViewById<EditText>(R.id.Apellido)
        correo = findViewById<EditText>(R.id.Correo)
        usuario = findViewById<EditText>(R.id.NomUsuario)
        contrasena = findViewById<EditText>(R.id.Contraseña)
        botonSoyRestaurante = findViewById(R.id.soyRestaurante)

        botonSoyRestaurante.setOnClickListener {clickSoyRestaurante()}

        //Crear listener para cuando se haga click en el TextView
        textIniciarSesion.setOnClickListener {
            val intent = Intent(this, InicioSesion::class.java)
            startActivity(intent)
        }

        botonCrearCuenta.setOnClickListener {
            validarCampos()
        }
    }

    fun clickSoyRestaurante(){
        val intent = Intent(this, CreacionCuentaRestaurante::class.java)
        startActivity(intent)
    }

    fun validarCampos(){
        val nombreText = nombre.text.toString()
        val apellidoText = apellido.text.toString()
        val correoText = correo.text.toString()
        val usuarioText = usuario.text.toString()
        val contrasenaText = contrasena.text.toString()

        if(nombreText.isNotEmpty() && apellidoText.isNotEmpty() && correoText.isNotEmpty() && usuarioText.isNotEmpty() && contrasenaText.isNotEmpty()){
            //Crear nuevo restaurante con valores por defecto
            var nuevoRestaurante = Restaurante("","",0.0,0.0,0.0, false)

            //Crear nuevo usuario con los valores introducidos
            var nuevoUsuario = Usuario(Random.nextInt(1000, 10000),usuarioText,nombreText,apellidoText,correoText,nuevoRestaurante, contrasenaText)

            createAccount(nuevoUsuario)

        }
        else
            Toast.makeText(this,"Ingrese los campos para continuar", Toast.LENGTH_SHORT).show()
    }

    fun createAccount(usuario: Usuario){
        Sesion.auth.createUserWithEmailAndPassword(usuario.email, usuario.contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("AUTH-SIGNUP", "createUserWithEmail:success")
                    val user = Sesion.auth.currentUser
                    storeUser(usuario)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("AUTH-SIGNUP", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun storeUser(usuario: Usuario) {
        val restaurante = hashMapOf(
            "calificacion" to usuario.restaurante.calificacion,
            "categoria" to usuario.restaurante.categoria,
            "latitud" to usuario.restaurante.latitud,
            "longitud" to usuario.restaurante.longitud,
            "nombre" to usuario.restaurante.nombre,
            "visibilidad" to usuario.restaurante.visibilidad
        )

        db.collection("restaurantes")
            .add(restaurante)
            .addOnSuccessListener { documentReference ->
                val restauranteId = documentReference.id
                Log.d("CREATE-USER", "DocumentSnapshot added with ID: $restauranteId")

                // Now create the "usuarios" document with the correct restauranteId
                val usuarioData = hashMapOf(
                    "apellido" to usuario.apellido,
                    "email" to usuario.email,
                    "nombre" to usuario.nombre,
                    "restaurante" to "restaurantes/$restauranteId",
                    "userName" to usuario.userName
                )

                db.collection("usuarios").add(usuarioData)
                    .addOnSuccessListener { userDocumentReference ->
                        Log.d("CREATE-USER", "Usuario document added with ID: ${userDocumentReference.id}")
                        val intent = Intent(this, InicioSesion::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.w("CREATE-USER", "Error adding usuario document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("CREATE-USER", "Error adding restaurante document", e)
            }
    }
}

