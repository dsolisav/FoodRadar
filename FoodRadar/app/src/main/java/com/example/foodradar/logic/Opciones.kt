package com.example.foodradar.logic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.foodradar.R
import com.example.foodradar.data.Data
import com.example.foodradar.data.Restaurant
import org.osmdroid.api.IMapController
import java.util.ArrayList
import com.example.foodradar.data.Sesion

class Opciones: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*enableEdgeToEdge()*/
        setContentView(R.layout.opciones)
        Data.RESTAURANT_ROUTE = ArrayList<Restaurant>()

        //Inicializacion de vistas
        val Hamburger = findViewById<ImageView>(R.id.Hamburger)
        val Sushi = findViewById<ImageView>(R.id.Sushi)
        val Pizza = findViewById<ImageView>(R.id.Pizza)

        Hamburger.setOnClickListener {
            TipoRestaurante("Hamburguesa")
        }
        Sushi.setOnClickListener {
            TipoRestaurante("Sushi")
        }
        Pizza.setOnClickListener {
            TipoRestaurante("Pizza")
        }


        /*
          ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
           }
         */

    }

    private fun  TipoRestaurante(tipo: String) {
        Sesion.restaurantMode = tipo
        val intentMapa = Intent(this, Mapa::class.java)
        startActivity(intentMapa)
    }

    override fun onResume() {
        super.onResume()
        Data.RESTAURANT_ROUTE = ArrayList<Restaurant>()
    }

}