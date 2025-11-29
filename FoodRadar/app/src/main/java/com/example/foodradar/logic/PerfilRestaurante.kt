package com.example.foodradar.logic

import ComentariosAdapter
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodradar.R
import com.example.foodradar.data.Comentario
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Sesion
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

class PerfilRestaurante : AppCompatActivity() {
    private lateinit var comentariosView: RecyclerView
    private lateinit var adapter: ComentariosAdapter
    lateinit var botonCalificarRestaurante: Button
    lateinit var restaurantName: String
    lateinit var categoria: String
    var calificacion = 0.0
    private var latitud by Delegates.notNull<Double>()
    private var longitud by Delegates.notNull<Double>()
    lateinit var restaurantId: String
    lateinit var calificacionRestaurante: TextView
    lateinit var direccion: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_restaurante)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        restaurantId = intent.getStringExtra("restaurantId").toString()
        restaurantName = intent.getStringExtra("restaurantName").toString()
        calificacion = intent.getDoubleExtra("puntaje",0.0)
        latitud = intent.getDoubleExtra("latitud", 0.0)
        longitud = intent.getDoubleExtra("longitud", 0.0)
        categoria = intent.getStringExtra("categoria").toString()
        Log.i("DesdePerfilRestaurante", "restaurantId: $restaurantId")

        val imagenRestaurante = findViewById<ImageView>(R.id.imagenRestaurante)

        if(categoria == "Hamburguesa"){
            imagenRestaurante.setImageResource(R.drawable.restaurant)
        }else if(categoria == "Sushi"){
            imagenRestaurante.setImageResource(R.drawable.sushirestaurant)
        }else if(categoria == "Pizza"){
            imagenRestaurante.setImageResource(R.drawable.pizzarestaurant)
        }

        val textoNombreRestaurante = findViewById<TextView>(R.id.textoNombreRestaurante)
        calificacionRestaurante = findViewById<TextView>(R.id.Calificacion)
        direccion = findViewById(R.id.direccion)

        textoNombreRestaurante.text = restaurantName
        calificacionRestaurante.text = calificacion.toString()

        comentariosView = findViewById(R.id.comentariosView)
        comentariosView.layoutManager = LinearLayoutManager(this)
        loadComentarios(this)
        botonCalificarRestaurante = findViewById<Button>(R.id.botonCalificarRestaurante)
        botonCalificarRestaurante.setOnClickListener{clickBotonCalificarRestaurante()}

        //Ocultar boton de calificar si el usuario es restaurante
        if(Sesion.esRestaurante == false) {
            botonCalificarRestaurante.visibility = View.VISIBLE
        }else{
            botonCalificarRestaurante.visibility = View.GONE
        }

        getLocationText(latitud, longitud)


    }

    fun clickBotonCalificarRestaurante(){
        var bundle = Bundle()
        Log.d("DesdePerfilRestaurante", "restaurantName: $restaurantName")
        Log.d("DesdePerfilRestaurante", "calificacion: $calificacion")
        bundle.putString("restaurantName",restaurantName)
        bundle.putDouble("puntaje",calificacion)
        bundle.putString("restaurantId",restaurantId)
        var intentCalificar = Intent(this, CalificarRestaurante::class.java)
        intentCalificar.putExtras(bundle)
        startActivity(intentCalificar)
    }

    override fun onResume() {
        super.onResume()
        loadComentarios(this)
    }

    private fun loadComentarios(context: Context) {
        val db = Firebase.firestore
        var comentarios : MutableList<Comentario> = mutableListOf()
        val restauranteRef = db.collection("restaurantes").document(restaurantId)
        val comentariosRef = db.collection("restaurantes").document(restaurantId).collection("comentarios")
        comentariosRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    // No comments found, handle this case (e.g., display a "No comments yet" message)
                    Log.d("GET-COMMENTS", "No comments yet for this restaurant")

                } else {
                    // Process the comments
                    var calificacionParaPromedio:Double = 0.0
                    for (document in snapshot.documents) {
                        val data = document.data ?: continue
                        calificacionParaPromedio += data["calificacion"] as? Double ?: 0.0
                        val nombreCompleto = data["nombre_completo"] as? String ?: ""
                        val calificacion = data["calificacion"].toString() as? String ?: ""
                        val fecha = data["fecha"] as? String ?: ""
                        val descripcion = data["descripcion"] as? String ?: ""
                        val imageUrl = data["imageUrl"] as? String ?: ""
                        val objetoComentario = Comentario(nombreCompleto, calificacion, fecha, descripcion, imageUrl)
                        comentarios.add(objetoComentario)
                    }
                    val dateFormat = SimpleDateFormat("dd-MM-yy HH:mm:ss")
                    val sortedComentarios = comentarios.sortedByDescending { comentario ->
                        dateFormat.parse(comentario.fecha)
                    }
                    val promedioCalificacionNumber = String.format("%.1f", calificacionParaPromedio / sortedComentarios.size).toDouble()
                    val promedioCalificacionText = String.format("%.1f", calificacionParaPromedio / sortedComentarios.size)
                    calificacionRestaurante.text = promedioCalificacionText
                    adapter = ComentariosAdapter(this, sortedComentarios)
                    comentariosView.adapter = adapter
                    restauranteRef.update("calificacion", promedioCalificacionNumber)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Campo actualizado con éxito")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error al actualizar el campo", e)
                        }

                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error retrieving comments", Toast.LENGTH_SHORT).show()

            }
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


    private data class ComentarioResponse(
        val comentarios: List<Comentario>
    )

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.drawer_menu, menu)
        menu?.findItem(R.id.miRestaurante)?.isVisible = Sesion.esRestaurante
        return super.onCreateOptionsMenu(menu)
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
}
