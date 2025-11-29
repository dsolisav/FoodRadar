package com.example.foodradar.logic

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.foodradar.R
import com.example.foodradar.data.Data
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Sesion
import com.example.foodradar.data.Restaurant
import com.example.foodradar.data.RestaurantesListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class Paradas: AppCompatActivity(), RestaurantesListener {

    private lateinit var Restaurante: String
    private lateinit var statusTextView: TextView
    private  lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var listView: ListView
    private lateinit var seleccion: Button



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*enableEdgeToEdge()*/
        setContentView(R.layout.paradas)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        seleccion = findViewById(R.id.botonCrearRuta)
        listView = findViewById<ListView>(R.id.lista)
        statusTextView = findViewById(R.id.Ubicacion)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Restaurante = intent.getStringExtra("TipoRestaurante") ?: ""
        Funciones.escucharRestaurantes(this, Restaurante) // `this` es un `RestaurantesListener`



        var ubicacion = Data.longitud?.let { Data.latitud?.let { it1 -> getLocationText(it1, it) } }

        statusTextView.text = "Ubicado en latitud $ubicacion"

        seleccion.setOnClickListener {
            val intent = Intent(this, Mapa::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        var ubicacion = Data.longitud?.let { Data.latitud?.let { it1 -> getLocationText(it1, it) } }
        statusTextView.text = "Ubicado en latitud $ubicacion"
        Funciones.escucharRestaurantes(this, Restaurante)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.drawer_menu, menu)
        //Ocultar boton si el usuario no es restaurante
        menu?.findItem(R.id.miRestaurante)?.isVisible = Sesion.esRestaurante
        return super.onCreateOptionsMenu(menu)
    }

    override fun onRestaurantesActualizados(listaRestaurantes: List<Restaurant>) {
        val sortedRestaurants = listaRestaurantes.sortedBy { restaurant ->
            Data.latitud?.let { lat ->
                Data.longitud?.let { lng -> Funciones.distance(lat, lng, restaurant.latitud, restaurant.longitud) }
            }
        }

        val adapter = RestaurantsAdapter(this, sortedRestaurants)
        listView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var intentCuenta = Intent(this, Perfil::class.java)
        var intentMiRestaurante = Intent(this, MiRestaurante::class.java)
        var intentInicio = Intent(this, Mapa::class.java)
        var intentCerrarSesion = Intent(this, InicioSesion::class.java)
        when(item.itemId){
            R.id.Cuenta -> startActivity(intentCuenta)
            R.id.miRestaurante -> startActivity(intentMiRestaurante)
            R.id.Inicio -> startActivity(intentInicio)
            R.id.cerrarSesion -> {
                Funciones.clearSesion()
                intentCerrarSesion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intentCerrarSesion)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getLocationText(latitude: Double, longitude: Double): String {
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

        return locationText
    }
}
