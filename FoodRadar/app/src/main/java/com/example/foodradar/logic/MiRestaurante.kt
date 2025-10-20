package com.example.foodradar.logic

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
// TO-DO ENTREGA FINAL import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.foodradar.R
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Sesion
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale
import java.util.concurrent.Executor

class MiRestaurante : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    lateinit var switchRestaurante: Switch
    private lateinit var nombreRestaurante: TextView
    lateinit var calificacion: TextView
    lateinit var direccion: TextView
    // TO-DO ENTREGA FINAL private lateinit var biometricPrompt: BiometricPrompt
    // TO-DO ENTREGA FINAL private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    private var isSwitchToggledManually = false // Flag to control switch toggling

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TO-DO ENTREGA FINAL setContentView(R.layout.activity_mi_restaurante)
        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        initializeUIElements()
        setupBiometricPrompt()
        setupSwitchListener()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater
        menuInflater.inflate(R.menu.drawer_menu, menu)
        //Ocultar boton si el usuario no es restaurante
        menu?.findItem(R.id.miRestaurante)?.isVisible = Sesion.esRestaurante
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TO-DO ENTREGA FINAL val intentCuenta = Intent(this, Perfil::class.java)
        // TO-DO ENTREGA FINAL val intentInicio = Intent(this, Mapa::class.java)
        // TO-DO ENTREGA FINAL var intentCerrarSesion = Intent(this, InicioSesion::class.java)
        when(item.itemId){
            // TO-DO ENTREGA FINAL R.id.Cuenta -> startActivity(intentCuenta)
            R.id.miRestaurante -> {}
            // TO-DO ENTREGA FINAL R.id.Inicio -> startActivity(intentInicio)
            R.id.cerrarSesion -> {
                Funciones.clearSesion()
                // TO-DO ENTREGA FINAL intentCerrarSesion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // TO-DO ENTREGA FINAL startActivity(intentCerrarSesion)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //Funcion para inicializar elementos de la interfaz
    private fun initializeUIElements() {
        val categoria = Sesion.restaurante["categoria"]
        // TO-DO ENTREGA FINAL val imagenRestaurante = findViewById<ImageView>(R.id.imagenRestaurante)

        if(categoria == "Hamburguesa"){
            // TO-DO ENTREGA FINAL imagenRestaurante.setImageResource(R.drawable.restaurant)
        }else if(categoria == "Sushi"){
            // TO-DO ENTREGA FINAL imagenRestaurante.setImageResource(R.drawable.sushirestaurant)
        }else if(categoria == "Pizza"){
            // TO-DO ENTREGA FINAL imagenRestaurante.setImageResource(R.drawable.pizzarestaurant)
        }


        nombreRestaurante = findViewById(R.id.nombreRestaurante)
        nombreRestaurante.text = Sesion.restaurante.get("nombre").toString()

        // TO-DO ENTREGA FINAL calificacion = findViewById(R.id.calificacion)
        calificacion.text = Sesion.restaurante.get("calificacion").toString()

        // TO-DO ENTREGA FINAL switchRestaurante = findViewById(R.id.switchRestaurante)
        //initial state for switch
        var initialVisibility = Sesion.restaurante["visibilidad"] as Boolean
        Log.i("RestauranteVisibility", initialVisibility.toString())
        if(initialVisibility == true){
            switchRestaurante.isChecked = true
            switchRestaurante.text = "Abierto"
            // TO-DO ENTREGA FINAL switchRestaurante.setTextColor(resources.getColor(R.color.verde))
        }else{
            switchRestaurante.isChecked = false
            switchRestaurante.text = "Cerrado"
            // TO-DO ENTREGA FINAL switchRestaurante.setTextColor(resources.getColor(R.color.rojo))
        }

        // TO-DO ENTREGA FINAL direccion = findViewById(R.id.direccionRestaurante)
        var latitude = Sesion.restaurante["latitud"] as Double
        var longitude = Sesion.restaurante["longitud"] as Double
        getLocationText(latitude, longitude)
    }

    private fun getLocationText(latitude: Double, longitude: Double) {
        var locationText = "No se pudo obtener la ubicación"  // Texto por defecto

        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                locationText = address.getAddressLine(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        direccion.text = locationText
    }

    //Funcion para inicializar/configurar prompt de huella
    private fun setupBiometricPrompt() {
        //  TO-DO ENTREGA FINAL DESCOMENTAR TODA ESTA FUNCION
        //  TO-DO ENTREGA FINAL executor = ContextCompat.getMainExecutor(this)
        // TO-DO ENTREGA FINAL biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
//            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                super.onAuthenticationSucceeded(result)
//                onBiometricSuccess() //Manejar autenticacion exitosa
//            }
//
//            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                super.onAuthenticationError(errorCode, errString)
//                onBiometricError(errString) //Manejar error de autenticacion
//            }
//
//            override fun onAuthenticationFailed() {
//                super.onAuthenticationFailed()
//                onBiometricFailed() //Manejar autenticacion fallida
//            }
        //  TO-DO ENTREGA FINAL })

//        promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Autenticacion Biométrica")
//            .setSubtitle("\nConfirma tu identidad para cambiar el estado del restaurante")
//            .setNegativeButtonText("Cancelar")
//            .build()
    }

    //Funcion para manejar el comportamiento del switch
    private fun setupSwitchListener() {
        switchRestaurante.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isSwitchToggledManually) {
                isSwitchToggledManually = false //Resetear bandera despues de toggle manual
            } else {
                switchRestaurante.isChecked = !isChecked //Revertir toggle
                // TO-DO ENTREGA FINAL biometricPrompt.authenticate(promptInfo) //Mostrar prompt biometrico
            }
        }
    }

    //Actualizar label y color del switch segun su estado
    @SuppressLint("SetTextI18n")
    private fun checkSwitch() {
        val stateChecked = switchRestaurante.isChecked
        if (stateChecked) {
            switchRestaurante.text = "Abierto"
            // TO-DO ENTREGA FINAL switchRestaurante.setTextColor(resources.getColor(R.color.verde))
        } else {
            switchRestaurante.text = "Cerrado"
            // TO-DO ENTREGA FINAL switchRestaurante.setTextColor(resources.getColor(R.color.rojo))
        }
    }

    //Manejar autenticacion sin exito
    private fun onBiometricSuccess() {
        runOnUiThread {
            isSwitchToggledManually = true //Permitir toggle manual del switch
            switchRestaurante.isChecked = !switchRestaurante.isChecked
            checkSwitch() //Actualizar switch

           // Actualizar visibilidad en Firebase
            val isAbierto = switchRestaurante.isChecked
            val db = Firebase.firestore
            val restauranteId = Sesion.restaurante["restaurantId"].toString()

            db.collection("restaurantes").document(restauranteId)
                .update("visibilidad", isAbierto)
                .addOnSuccessListener {
                    val estado = if (isAbierto) "abierto" else "cerrado"
                    Toast.makeText(this, "El restaurante ahora está $estado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al cambiar la visibilidad: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //Manejar error de autenticacion
    private fun onBiometricError(errString: CharSequence) {
        Toast.makeText(this@MiRestaurante, "Error de autenticación: $errString", Toast.LENGTH_SHORT).show()
    }

    //Manejar autenticacion fallida
    private fun onBiometricFailed() {
        Toast.makeText(this@MiRestaurante, "Autenticación fallida", Toast.LENGTH_SHORT).show()
    }
}
