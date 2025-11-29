package com.example.foodradar.logic

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.foodradar.R
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Sesion
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale

class MiRestaurante : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    lateinit var switchRestaurante: Switch
    private lateinit var nombreRestaurante: TextView
    lateinit var calificacion: TextView
    lateinit var direccion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_restaurante)
        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        initializeUIElements()
        loadRestaurantVisibility() // Cargar visibilidad actual desde Firebase
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
        val intentCuenta = Intent(this, Perfil::class.java)
        val intentInicio = Intent(this, Mapa::class.java)
        var intentCerrarSesion = Intent(this, InicioSesion::class.java)
        when(item.itemId){
            R.id.Cuenta -> startActivity(intentCuenta)
            R.id.miRestaurante -> {}
            R.id.Inicio -> startActivity(intentInicio)
            R.id.cerrarSesion -> {
                Funciones.clearSesion()
                intentCerrarSesion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intentCerrarSesion)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //Funcion para inicializar elementos de la interfaz
    private fun initializeUIElements() {
        val categoria = Sesion.restaurante["categoria"]
        val imagenRestaurante = findViewById<ImageView>(R.id.imagenRestaurante)

        if(categoria == "Hamburguesa"){
            imagenRestaurante.setImageResource(R.drawable.restaurant)
        }else if(categoria == "Sushi"){
            imagenRestaurante.setImageResource(R.drawable.sushirestaurant)
        }else if(categoria == "Pizza"){
            imagenRestaurante.setImageResource(R.drawable.pizzarestaurant)
        }


        nombreRestaurante = findViewById(R.id.nombreRestaurante)
        nombreRestaurante.text = Sesion.restaurante.get("nombre").toString()

        calificacion = findViewById(R.id.calificacion)
        calificacion.text = Sesion.restaurante.get("calificacion").toString()

        switchRestaurante = findViewById(R.id.switchRestaurante)
        //initial state for switch
        var initialVisibility = Sesion.restaurante["visibilidad"] as Boolean
        Log.i("RestauranteVisibility", initialVisibility.toString())
        if(initialVisibility == true){
            switchRestaurante.isChecked = true
            switchRestaurante.text = "Abierto"
            switchRestaurante.setTextColor(resources.getColor(R.color.verde))
        }else{
            switchRestaurante.isChecked = false
            switchRestaurante.text = "Cerrado"
            switchRestaurante.setTextColor(resources.getColor(R.color.rojo))
        }

        direccion = findViewById(R.id.direccionRestaurante)
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

    //Funcion para manejar el comportamiento del switch
    private fun setupSwitchListener() {
        switchRestaurante.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            updateRestaurantVisibility(isChecked)
        }
    }

    //Cargar visibilidad actual desde Firebase
    private fun loadRestaurantVisibility() {
        val db = Firebase.firestore
        val restauranteId = Sesion.restaurante["restaurantId"].toString()

        db.collection("restaurantes").document(restauranteId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val visibilidad = document.getBoolean("visibilidad") ?: false
                    Sesion.restaurante["visibilidad"] = visibilidad
                    
                    // Actualizar switch sin disparar el listener
                    switchRestaurante.setOnCheckedChangeListener(null)
                    switchRestaurante.isChecked = visibilidad
                    if (visibilidad) {
                        switchRestaurante.text = "Abierto"
                        switchRestaurante.setTextColor(resources.getColor(R.color.verde))
                    } else {
                        switchRestaurante.text = "Cerrado"
                        switchRestaurante.setTextColor(resources.getColor(R.color.rojo))
                    }
                    // Volver a configurar el listener
                    setupSwitchListener()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MiRestaurante", "Error al cargar visibilidad: ${exception.message}")
            }
    }

    //Actualizar label y color del switch segun su estado
    @SuppressLint("SetTextI18n")
    private fun checkSwitch() {
        val stateChecked = switchRestaurante.isChecked
        if (stateChecked) {
            switchRestaurante.text = "Abierto"
            switchRestaurante.setTextColor(resources.getColor(R.color.verde))
        } else {
            switchRestaurante.text = "Cerrado"
            switchRestaurante.setTextColor(resources.getColor(R.color.rojo))
        }
    }

    //Actualizar visibilidad del restaurante en Firebase
    private fun updateRestaurantVisibility(isAbierto: Boolean) {
        checkSwitch()
        
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
