package com.example.foodradar.logic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.foodradar.R
import com.example.foodradar.data.Data
import com.example.foodradar.data.Restaurant

class RestaurantsAdapter(context: Context?, private val restaurantes: List<Restaurant>): ArrayAdapter<Restaurant>(context!!, 0, restaurantes) {
    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Obtener el restaurante en la posición actual
        val restaurante = getItem(position)

        // Usar el convertView para reutilizar la vista si es posible, si no, inflar una nueva
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.layout_restaurants, parent, false)

        // Obtener las referencias a las vistas dentro del layout
        val nombreRestaurante = view.findViewById<TextView>(R.id.Restaurante)
        val categoriaRestaurante = view.findViewById<ImageView>(R.id.logo)
        val calificacionRestaurante = view.findViewById<TextView>(R.id.Calificacion)
        val perfilButton = view.findViewById<TextView>(R.id.Perfil)
        val añadirButton = view.findViewById<Button>(R.id.Añadir)

        // Poblar los datos del restaurante en las vistas
        nombreRestaurante.text = restaurante?.nombre
        calificacionRestaurante.text = restaurante?.calificacion.toString()
        when(restaurante?.categoria){
            "Pizza" -> categoriaRestaurante.setImageResource(R.drawable.pizza)
            "Hamburguesa"-> categoriaRestaurante.setImageResource(R.drawable.hamburegesa)
            "Sushi" -> categoriaRestaurante.setImageResource(R.drawable.sushi)
        }

        perfilButton.setOnClickListener {
            // TO-DO ENTREGA FINAL val intent = Intent(context, PerfilRestaurante::class.java)
            // TO-DO ENTREGA FINAL intent.putExtra("restaurantId", restaurante?.id)
            // TO-DO ENTREGA FINAL intent.putExtra("restaurantName", restaurante?.nombre)
            // TO-DO ENTREGA FINAL intent.putExtra("puntaje",restaurante?.calificacion)
            // TO-DO ENTREGA FINAL intent.putExtra("latitud", restaurante?.latitud)
            // TO-DO ENTREGA FINAL intent.putExtra("longitud",restaurante?.longitud)
            // TO-DO ENTREGA FINAL intent.putExtra("categoria", restaurante?.categoria)
            // TO-DO ENTREGA FINAL context.startActivity(intent)
        }

        if (Data.RESTAURANT_ROUTE.contains(restaurante)){
                añadirButton.setBackgroundColor(Color.GREEN)
            añadirButton.text = "Cancelar"
        }


        añadirButton.setOnClickListener {
            if (Data.RESTAURANT_ROUTE.contains(restaurante)){
                Data.RESTAURANT_ROUTE.remove(restaurante)
                añadirButton.setBackgroundColor(Color.RED)
                añadirButton.text = "Añadir"
                Toast.makeText(context,"Restaurante eliminado", Toast.LENGTH_SHORT).show()
            }else {
                if (restaurante != null) {
                    Data.RESTAURANT_ROUTE.add(restaurante)
                }
                añadirButton.setBackgroundColor(Color.GREEN)
                añadirButton.text = "Cancelar"
                Toast.makeText(context,"Restaurante añadido", Toast.LENGTH_SHORT).show()
            }

        }


        return view
    }
}