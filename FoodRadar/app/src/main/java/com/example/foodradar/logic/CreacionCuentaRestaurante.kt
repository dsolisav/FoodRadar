package com.example.foodradar.logic

import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodradar.R
import com.example.foodradar.data.Data
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Restaurante
import com.example.foodradar.data.Sesion
import com.example.foodradar.data.Usuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale
import kotlin.random.Random

class CreacionCuentaRestaurante : AppCompatActivity() {

    private lateinit var textIniciarSesion : TextView
    private lateinit var botonCrearCuentaRestaurante : Button
    private lateinit var nombre : EditText
    private lateinit var apellido : EditText
    private lateinit var correo : EditText
    private lateinit var usuario : EditText
    private lateinit var contrasena : EditText
    private lateinit var nombreRestaurante : EditText
    private lateinit var spinnerCategoria : Spinner
    private lateinit var ubicacion : EditText
    private lateinit var botonMapa: Button
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creacion_cuenta_restaurante)
        textIniciarSesion = findViewById(R.id.InicioSesion)
        botonCrearCuentaRestaurante = findViewById(R.id.BotonCrearCuenta)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        nombre = findViewById(R.id.Nombre)
        apellido = findViewById(R.id.Apellido)
        correo = findViewById(R.id.Correo)
        usuario = findViewById(R.id.NomUsuario)
        contrasena = findViewById(R.id.Contraseña)
        nombreRestaurante = findViewById(R.id.nombreRestaurante)
        ubicacion = findViewById(R.id.Ubicacion)
        botonMapa = findViewById(R.id.BotonMapa)


        ubicacion.isEnabled = false
        ubicacion.isClickable = false
        Log.d("TILIN", "en Oncreate Latitud: ${Data.latitud}, Longitud: ${Data.longitud}")


        if (Data.latitud != null && Data.longitud != null){
            var direction = Data.longitud?.let { Data.latitud?.let { it1 -> getLocationText(it1, it) } }
            ubicacion.setText(direction)
        }


        textIniciarSesion.setOnClickListener {
            val intent = Intent(this, InicioSesion::class.java)
            startActivity(intent)
        }

        botonCrearCuentaRestaurante.setOnClickListener{
            validarCampos()
        }

        botonMapa.setOnClickListener {
            val intent = Intent(this, MapaRestaurante::class.java)
            startActivityForResult(intent, Data.MY_PERMISSION_LOCATION_CODE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Data.MY_PERMISSION_LOCATION_CODE && resultCode == RESULT_OK && data != null) {
            val latitud = data.getDoubleExtra("latitud", 0.0)
            val longitud = data.getDoubleExtra("longitud", 0.0)

            // Guarda los valores en tu clase Data
            Data.latitud = latitud
            Data.longitud = longitud
            Log.d("TILIN", "recuperado del intent Latitud: ${Data.latitud}, Longitud: ${Data.longitud}")

            // Actualiza el campo de texto
            val direccion = getLocationText(latitud, longitud)
            ubicacion.setText(direccion) // Actualizar el texto del EditText
        } else {
            Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
        }
    }



    private fun validarCampos(){
        val nombreText = nombre.text.toString()
        val apellidoText = apellido.text.toString()
        val correoText = correo.text.toString()
        val usuarioText = usuario.text.toString()
        val contrasenaText = contrasena.text.toString()
        val nombreRestauranteText = nombreRestaurante.text.toString()
        val ubicacionText = ubicacion.text.toString()
        if(nombreText.isNotEmpty() && apellidoText.isNotEmpty() && correoText.isNotEmpty() && usuarioText.isNotEmpty() && contrasenaText.isNotEmpty() && nombreRestauranteText.isNotEmpty() && ubicacionText.isNotEmpty() && Data.latitud != null && Data.longitud != null){
            //Crear nuevo restaurante
            var nuevoRestaurante =
                Data.latitud?.let {
                    Data.longitud?.let { it1 ->
                        Restaurante(nombreRestauranteText,spinnerCategoria.selectedItem.toString(),0.0,
                            it1,
                            it, false
                        )
                    }
                }

            //Crear nuevo usuario con los valores introducidos
            var nuevoUsuario = nuevoRestaurante?.let {
                Usuario(Random.nextInt(1000, 10000),usuarioText,nombreText,apellidoText,correoText, it, contrasenaText)
            }

            if (nuevoUsuario != null) {
                createAccount(nuevoUsuario)
            }else{
                Toast.makeText(this,"Error al crear la cuenta", Toast.LENGTH_SHORT).show()
            }



        }else
            Toast.makeText(this,"Ingrese los campos para continuar", Toast.LENGTH_SHORT).show()

    }

    private fun getLocationText(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Log.d("TILIN", "Dirección obtenida: ${address.getAddressLine(0)}")
                address.getAddressLine(0)
            } else {
                Log.d("TILIN", "No se encontraron direcciones para las coordenadas: $latitude, $longitude")
                "No se pudo obtener la ubicación"
            }
        } catch (e: Exception) {
            Log.e("TILIN", "Error al obtener la dirección: ${e.message}")
            "No se pudo obtener la ubicación"
        }
    }


    fun createAccount(usuario: Usuario){
        Sesion.auth.createUserWithEmailAndPassword(usuario.email, usuario.contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("AUTH-SIGNUP", "createUserWithEmail:success")
                    val user = Sesion.auth.currentUser
                    storeUser(usuario)
                    val intent = Intent(this, InicioSesion::class.java)
                    startActivity(intent)

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
        Log.d("CREATE-USER", "Llegue hasta aca")
        Log.d("CREATE-USER", "Restaurante: $restaurante")

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
                        Toast.makeText(this,"Cuenta existosamente creada", Toast.LENGTH_SHORT).show()

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