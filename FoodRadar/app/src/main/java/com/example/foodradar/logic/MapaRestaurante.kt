package com.example.foodradar.logic

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodradar.R
import com.example.foodradar.data.Data
// TO-DO ENTREGA FINAL import com.google.android.gms.location.FusedLocationProviderClient
// TO-DO ENTREGA FINAL import com.google.android.gms.location.LocationCallback
// TO-DO ENTREGA FINAL import com.google.android.gms.location.LocationRequest
// TO-DO ENTREGA FINAL import com.google.android.gms.location.LocationResult
// TO-DO ENTREGA FINAL import com.google.android.gms.location.LocationServices
// TO-DO ENTREGA FINAL import com.google.android.gms.location.LocationSettingsRequest
// TO-DO ENTREGA FINAL import com.google.android.gms.location.LocationSettingsResponse
// TO-DO ENTREGA FINAL import com.google.android.gms.location.Priority
// TO-DO ENTREGA FINAL import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
//  TO-DO ENTREGA FINAL import org.osmdroid.api.IMapController
//  TO-DO ENTREGA FINAL import org.osmdroid.config.Configuration
//  TO-DO ENTREGA FINAL import org.osmdroid.tileprovider.tilesource.TileSourceFactory
//  TO-DO ENTREGA FINAL import org.osmdroid.util.GeoPoint
//  TO-DO ENTREGA FINAL import org.osmdroid.views.MapView
//  TO-DO ENTREGA FINAL import org.osmdroid.views.overlay.MapEventsOverlay
//  TO-DO ENTREGA FINAL import org.osmdroid.views.overlay.Marker
//  TO-DO ENTREGA FINAL import org.osmdroid.views.overlay.Overlay
//  TO-DO ENTREGA FINAL import org.osmdroid.views.overlay.compass.CompassOverlay
import java.io.IOException
import android.os.Handler
import android.view.MotionEvent
import java.util.Locale


class MapaRestaurante: AppCompatActivity() {

    //  TO-DO ENTREGA FINAL private lateinit var mapView: MapView
    //  TO-DO ENTREGA FINAL private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var directionText: EditText
    private lateinit var boton: Button
    private lateinit var direccion: String
    //  TO-DO ENTREGA FINAL private lateinit var userMarker: Marker
    //  TO-DO ENTREGA FINAL private var direccionMarker: Marker? = null
    //  TO-DO ENTREGA FINAL private var savedMarker: Marker? = null
    //  TO-DO ENTREGA FINAL private lateinit var compassOverlay: Overlay
    private lateinit var geocoder: Geocoder
    private lateinit var handler: Handler
    private var isLongPress = false
    private var x = 0f
    private var y = 0f
    // TO-DO ENTREGA FINAL private var longPressMarker: Marker? = null

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*enableEdgeToEdge()*/
        setContentView(R.layout.mapa_restaurante)

        //Inicializar el contexto para osmdroid
        // TO-DO ENTREGA FINAL Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // TO-DO ENTREGA FINAL mapView = findViewById(R.id.osmMapRestaurante)
        directionText = findViewById(R.id.etDireccion)
        boton = findViewById(R.id.BotonMapa)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val algo = Data.MY_PERMISSION_LOCATION_CODE

        // TO-DO ENTREGA FINAL mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        pedirPermiso(this, Manifest.permission.ACCESS_FINE_LOCATION, "Acceso a Ubicacion",algo)
        pedirPermiso(this, Manifest.permission.ACCESS_COARSE_LOCATION, "Acceso a Ubicacion",algo)

        geocoder = Geocoder(this)  // Inicializar el Geocoder

        // Evento para buscar la dirección cuando el usuario termina de editar el EditText
        directionText.setOnEditorActionListener { _, _, _ ->
            direccion = directionText.text.toString()
            if (direccion.isNotEmpty()) {
                buscarDireccion(direccion)
            }
            false
        }

        handler = Handler()

        // Runnable para detectar el long press
        val longPressRunnable = Runnable {
            isLongPress = true
            // TO-DO ENTREGA FINAL
//            longPressMarker?.let {
//                mapView.overlays.remove(it)
//            }

            // TO-DO ENTREGA FINAL val point = mapView.projection.fromPixels(x.toInt(), y.toInt()) as GeoPoint
            // TO-DO ENTREGA FINAL val marker = Marker(mapView)
            // TO-DO ENTREGA FINAL marker.position = point
            // TO-DO ENTREGA FINAL mapView.overlays.add(marker)
            // TO-DO ENTREGA FINAL mapView.invalidate()

            // TO-DO ENTREGA FINAL longPressMarker = marker

            // TO-DO ENTREGA FINAL // Obtener la dirección utilizando Geocoder
            // TO-DO ENTREGA FINAL Data.latitud= point.latitude
            // TO-DO ENTREGA FINAL Data.longitud= point.longitude
            getAddressFromCoordinates(Data.latitud!!, Data.longitud!!)
        }

        // Configura el listener de toque en el mapa
        // TO-DO ENTREGA FINAL
//        mapView.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    // Guarda las coordenadas del toque
//                    x = event.x
//                    y = event.y
//                    isLongPress = false
//                    // Comienza a contar el tiempo de presión
//                    handler.postDelayed(longPressRunnable, 2000) // Detecta después de 2 segundos
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    // Cancela el long press si el usuario levanta el dedo antes de 1 segundo
//                    handler.removeCallbacks(longPressRunnable)
//                }
//            }
//            // Deja que el mapa siga respondiendo al movimiento (retorna true)
//            return@setOnTouchListener false
        }

        // Habilita el desplazamiento y zoom con gestos
        // TO-DO ENTREGA FINAL mapView.setMultiTouchControls(true)


        /*
          ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
           }
         */

        // TO-DO ENTREGA FINAL
//        boton.setOnClickListener {
//            // Enviar la direccion del restaurante para luego convertir en longitud y latitud
//            val intent = Intent()
//            intent.putExtra("latitud", Data.latitud)
//            intent.putExtra("longitud", Data.longitud)
//            setResult(RESULT_OK, intent) // Envía los datos de vuelta
//            finish() // Finaliza la actividad para regresar a la principal
//
//
//        }

    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double) {
        // TO-DO ENTREGA FINAL
//        val geocoder = Geocoder(this, Locale.getDefault())
//        try {
//            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//            if (addresses != null) {
//                if (addresses.isNotEmpty()) {
//                    val address = addresses[0]
//                    val addressText = StringBuilder()
//                    if (address != null) {
//                        for (i in 0..address.maxAddressLineIndex) {
//                            addressText.append(address.getAddressLine(i)).append("\n")
//                        }
//                    }
//                    // Asignar el texto generado al EditText
//                    // TO-DO ENTREGA FINAL directionText.setText(addressText.toString())
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            // TO-DO ENTREGA FINAL Toast.makeText(this, "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show()
//        }
    }



    // Función para buscar una dirección y agregar el marcador sin borrar el marcador actual
    private fun buscarDireccion(direccion: String) {
//        try {
            // Buscar la dirección usando el Geocoder
            // TO-DO ENTREGA FINAL
//            val resultados = geocoder.getFromLocationName(direccion, 1)
//            if (!resultados.isNullOrEmpty()) {
//                val location = resultados[0]
//                val destino = GeoPoint(location.latitude, location.longitude)
//                Data.latitud = location.latitude
//                Data.longitud = location.longitude
//
//                // Borrar el marcador del destino anterior, si existe
//                if (direccionMarker != null) {
//                    mapView.overlays.remove(direccionMarker)
//                }
//
//                // Borrar el marcador del long press anterior, si existe
//                if (longPressMarker !=null){
//                    mapView.overlays.remove(longPressMarker)
//                }
//
//                // Crear y agregar un nuevo marcador para el destino
//                direccionMarker = Marker(mapView).apply {
//                    position = destino
//                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                    title = direccion
//                }
//                mapView.overlays.add(direccionMarker)
//
//                // Mover la cámara a la nueva ubicación del destino
//                mapView.controller.animateTo(destino)
//                mapView.controller.setZoom(15.0)
//
//                // Refrescar el mapa
//                mapView.invalidate()
//            } else {
//                Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: IOException) {
//            Toast.makeText(this, "Error al buscar la dirección", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun pedirPermiso(context: Activity, permiso: String,justificacion: String, idCode: Int) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                permiso
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permiso ya concedido, usar la ubicación
                setLocation()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(context, permiso) -> {
                // Mostrar justificación y solicitar permiso
                showPermissionRationale()
                ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
            }

            else -> {
                // Solicitar permiso directamente
                // TO-DO ENTREGA FINAL ActivityCompat.requestPermissions(this, arrayOf(permiso), idCode)
            }
        }
    }
// TO-DO ENTREGA FINAL
//    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            requestCode -> {
//                // Si el permiso fue cancelado, el arreglo de permisos esta vacio
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // El permiso fue concedido, usar ubicacion
//                    setLocation()
//                } else {
//                    // Mostrar estado de permiso denegado
//                    showPermissionStatus(false)
//                }
//                return
//            }
//
//            else -> {
//                // Ignore all other requests.
//            }
//        }
//    }


    private fun setLocation() {
        // Verifica permisos antes de intentar acceder a la ubicación
        // TO-DO ENTREGA FINAL
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//
//            mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
//                if (location != null) {
//                    ubicarRestaurante(location)
//                    showPermissionStatus(true)
//                }
//            }
//        } else {
//            // Mostrar estado de permiso denegado
//            showPermissionStatus(false)
//        }
    }

    private fun showPermissionRationale() {
        // TO-DO ENTREGA FINAL
//        Toast.makeText(
//            this, "Servicios reducidos", Toast.LENGTH_LONG).show()
    }

    private fun showPermissionStatus(granted: Boolean) {
        // TO-DO ENTREGA FINAL
//        if (!granted) {
//            boton.visibility = View.GONE
//        } else {
//            boton.visibility = View.VISIBLE
//        }
    }

    private fun ubicarRestaurante(location: Location) {

        // Verifica si hay una ubicación guardada en el companion object
        Log.d("TILIN", " al entrar a la ubicacion Latitud: ${Data.latitud}, Longitud: ${Data.longitud}")
        val savedLat = Data.latitud
        val savedLon = Data.longitud

        if (savedLat != null && savedLon != null) {
            // TO-DO ENTREGA FINAL
//            val savedLocation = GeoPoint(savedLat, savedLon)
//            mapView.controller.setCenter(savedLocation)
//            mapView.controller.setZoom(15.0)
//
//            // Añadir marcador si es necesario
//            savedMarker = Marker(mapView)
//            savedMarker!!.position = savedLocation
//            savedMarker!!.icon = crearMarcador(Color.BLUE) // Cambia el color del marcador si es necesario
//            savedMarker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//            savedMarker!!.title = "Ubicación guardada"
//            mapView.overlays.add(savedMarker)
//
//            mapView.invalidate()
        }else{
            // TO-DO ENTREGA FINAL val userLocation = GeoPoint(location.latitude, location.longitude)
            Data.latitud = location.latitude
            Data.longitud = location.longitude

            // TO-DO ENTREGA FINAL mapView.setTileSource(TileSourceFactory.MAPNIK)
            // TO-DO ENTREGA FINAL mapView.setBuiltInZoomControls(true)
            // TO-DO ENTREGA FINAL mapView.setMultiTouchControls(true)

            //Ubicar el mapa en la ubicación del usuario
            // TO-DO ENTREGA FINAL mapView.controller.setZoom(15.0)
            // TO-DO ENTREGA FINAL mapView.controller.setCenter(userLocation)

            // Añadir un marcador en la ubicación del usuario
            // TO-DO ENTREGA FINAL userMarker = Marker(mapView)
            // TO-DO ENTREGA FINAL userMarker.position = userLocation
            // TO-DO ENTREGA FINAL userMarker.icon = crearMarcador(Color.RED)
            // TO-DO ENTREGA FINAL userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            // TO-DO ENTREGA FINAL userMarker.title = "Tu ubicación"
            // TO-DO ENTREGA FINAL userMarker.alpha = 1.0f
            // TO-DO ENTREGA FINAL mapView.overlays.add(userMarker)

            // TO-DO ENTREGA FINAL compassOverlay = CompassOverlay(this, mapView)
            // TO-DO ENTREGA FINAL (compassOverlay as CompassOverlay).enableCompass()
            // TO-DO ENTREGA FINAL mapView.overlays.add(compassOverlay)


            //Refrescar el mapa
            // TO-DO ENTREGA FINAL mapView.invalidate()

        }

    }

    private fun crearMarcador(color: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(color)
        drawable.setSize(30, 30)  // Tamaño del ícono
        return drawable
    }

    // TO-DO ENTREGA FINAL
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//        val mapController: IMapController = mapView.controller
//        mapController.setZoom(18.0)
//
//        // Intentar obtener la ubicación y centrar el mapa en la reanudación de la actividad
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            setLocation()
//        }
//
//
//    }
//
//
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }




// TO-DO ENTREGA FINAL }