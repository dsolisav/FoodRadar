package com.example.foodradar.logic

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
// TO-DO ENTREGA FINAL import org.osmdroid.config.Configuration
// TO-DO ENTREGA FINAL import org.osmdroid.tileprovider.tilesource.TileSourceFactory
// TO-DO ENTREGA FINAL import org.osmdroid.util.GeoPoint
// TO-DO ENTREGA FINAL import org.osmdroid.views.MapView
// TO-DO ENTREGA FINAL import org.osmdroid.views.overlay.Marker
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodradar.R
import com.example.foodradar.data.Data
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Restaurant
import com.example.foodradar.data.RestaurantesListener
import com.example.foodradar.data.Sesion
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
// TO-DO ENTREGA FINAL import org.osmdroid.api.IMapController
// TO-DO ENTREGA FINAL import org.osmdroid.bonuspack.routing.OSRMRoadManager
// TO-DO ENTREGA FINAL import org.osmdroid.bonuspack.routing.Road
// TO-DO ENTREGA FINAL import org.osmdroid.bonuspack.routing.RoadManager
// TO-DO ENTREGA FINAL import org.osmdroid.views.overlay.Overlay
// TO-DO ENTREGA FINAL import org.osmdroid.views.overlay.Polyline
// TO-DO ENTREGA FINAL import org.osmdroid.views.overlay.compass.CompassOverlay

class Mapa: AppCompatActivity(), RestaurantesListener {

    private lateinit var statusTextView: TextView
    // TO-DO ENTREGA FINAL private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var Restaurante: String
    private lateinit var boton: Button
    private lateinit var Button: Button
    // TO-DO ENTREGA FINAL private lateinit var mapView: MapView
    // TO-DO ENTREGA FINAL private lateinit var compassOverlay: Overlay
    // TO-DO ENTREGA FINAL private lateinit var mLocationRequest: LocationRequest
    // TO-DO ENTREGA FINAL private lateinit var mLocationCallback: LocationCallback
    // TO-DO ENTREGA FINAL private lateinit var roadManager: RoadManager
    // TO-DO ENTREGA FINAL private var roadOverlay: Polyline? = null
    // TO-DO ENTREGA FINAL private var userMarker: Marker? = null //Variable para almaenar marcador actual
    // TO-DO ENTREGA FINAL private var listaMarkerRest = mutableListOf<Marker>() //Almacenar los marcadores de restaurantes
    private val getLocationSettings = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        Log.i("LOCATION", "Result from settings: ${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            var settingsOK = true
            startLocationUpdates()
        } else {
            statusTextView.text = "GPS is off"
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*enableEdgeToEdge()*/

        //Inicializar el contexto para osmdroid
        // TO-DO ENTREGA FINAL Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        setContentView(R.layout.mapa)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        // TO-DO ENTREGA FINAL mapView = findViewById(R.id.osmMap)
        // TO-DO ENTREGA FINAL roadManager = OSRMRoadManager(this, "ANDROID")

        val algo = Data.MY_PERMISSION_LOCATION_CODE

        statusTextView = findViewById(R.id.textView5)

        // TO-DO ENTREGA FINAL mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // TO-DO ENTREGA FINAL mLocationRequest = createLocationRequest()
        // TO-DO ENTREGA FINAL
//        mLocationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                val location = locationResult.lastLocation
//                Log.i("LOCATION", "Location update in the callback: $location")
//                if (location != null) {
//                    actualizarUbicacion(location)
//                }
//            }
//        }


        // Recibir el tipo de restaurante seleccionado
        Restaurante = Sesion.restaurantMode
        Funciones.escucharRestaurantes(this, Restaurante) // `this` es un `RestaurantesListener`


        Button = findViewById(R.id.button)
        boton = findViewById(R.id.botonCentrar)

        statusTextView.text = "Buscando restaurantes de: $Restaurante en la zona"
        statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))


        pedirPermiso(this, Manifest.permission.ACCESS_FINE_LOCATION, "Acceder Ubicacion", algo)
        pedirPermiso(this, Manifest.permission.ACCESS_COARSE_LOCATION, "Acceder Ubicacion", algo)
        checkLocationSettings()


        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        */



        boton.setOnClickListener {
            //Ubicar el mapa en la ubicación del usuario
            // TO-DO ENTREGA FINAL
//            mapView.controller.setZoom(15.0)
//            mapView.controller.setCenter(Data.latitud?.let { it1 -> Data.longitud?.let { it2 ->
//                GeoPoint(it1,
//                    it2
//                )
//            } })

        }


    }

    override fun onRestaurantesActualizados(listaRestaurantes: List<Restaurant>) {
        if (Data.latitud == null || Data.longitud == null) {
            Log.e("onRestaurantesActualizados", "Latitud o longitud no están disponibles")
            return // Termina la ejecución si los valores son null
        }
        val location = Location("dummyprovider") // Puedes usar un nombre cualquiera para el proveedor
        location.latitude = Data.latitud!!
        location.longitude = Data.longitud!!
        buscarRestaurante(location)
    }

    private fun botonHabilitado(){
        Button.isEnabled = true
        Button.setOnClickListener {

            val intentParadas = Intent(this, Paradas::class.java).apply {
                putExtra("TipoRestaurante", Restaurante)
            }
            startActivity(intentParadas)
        }
    }

    private fun actualizarUbicacion(location: Location) {
        // TO-DO ENTREGA FINAL val userLocation = GeoPoint(location.latitude, location.longitude)
        Data.latitud = location.latitude
        Data.longitud = location.longitude

        // TO-DO ENTREGA FINAL val waypoints = ArrayList<GeoPoint>()
        // TO-DO ENTREGA FINAL mapView.overlays.remove(roadOverlay) // Elimina el overlay de la ruta
        // TO-DO ENTREGA FINAL mapView.invalidate() // Refresca el mapa


        // TO-DO ENTREGA FINAL waypoints.add(userLocation)


        // TO-DO ENTREGA FINAL
//        if (userMarker != null) {
//            userMarker?.remove(mapView)
//        }

        // Añadir un marcador en la ubicación del usuario
        // TO-DO ENTREGA FINAL userMarker = Marker(mapView)
        // TO-DO ENTREGA FINAL userMarker?.position = userLocation
        // TO-DO ENTREGA FINAL userMarker?.icon = crearMarcador(Color.RED)
        // TO-DO ENTREGA FINAL userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        // TO-DO ENTREGA FINAL userMarker?.title = "Tu ubicación"
        // TO-DO ENTREGA FINAL userMarker?.alpha = 1.0f
        // TO-DO ENTREGA FINAL mapView.overlays.add(userMarker)

        // Obtener lista ordenada por proximidad
        val sortedRestaurants = getRestaurantsByProximity(location, Data.RESTAURANT_ROUTE)

        // Agregar los restaurantes como puntos en la lista
        sortedRestaurants.forEach { restaurant ->
            // TO-DO ENTREGA FINAL if(Data.RESTAURANT_LIST.contains(restaurant)){
            // TO-DO ENTREGA FINAL waypoints.add(GeoPoint(restaurant.latitud,restaurant.longitud))
            // TO-DO ENTREGA FINAL }
        }

        // TO-DO ENTREGA FINAL val road = roadManager.getRoad(waypoints)

        // TO-DO ENTREGA FINAL if (road.mStatus != Road.STATUS_OK) {
        // TO-DO ENTREGA FINAL Toast.makeText(this, "Error al obtener la ruta", Toast.LENGTH_SHORT).show()
        // TO-DO ENTREGA FINAL     return
        // TO-DO ENTREGA FINAL }

        // Calcular tiempo estimado de la ruta
        // TO-DO ENTREGA FINAL val duracion = road.mDuration
        // TO-DO ENTREGA FINAL val totalDurationInMinutes = duracion / 60
        // TO-DO ENTREGA FINAL val redondeado = kotlin.math.round(totalDurationInMinutes)

        // TO-DO ENTREGA FINAL statusTextView.text = "Duracion Estimada Recorrido: $redondeado minutos"
        statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))

        // Dibujar la ruta en el mapa
        // TO-DO ENTREGA FINAL
//        if (waypoints.size > 1){
//            if (mapView != null) {
//                roadOverlay?.let {mapView.overlays.remove(it) }
//                roadOverlay = RoadManager.buildRoadOverlay(road)
//                roadOverlay?.outlinePaint?.color = Color.MAGENTA
//                roadOverlay?.outlinePaint?.strokeWidth = 10f
//                mapView.overlays.add(roadOverlay)
//
//                //Refrescar el mapa
//                mapView.invalidate()
//            }
//        }


        boton.setOnClickListener {
            //Ubicar el mapa en la ubicación del usuario
            // TO-DO ENTREGA FINAL mapView.controller.setZoom(15.0)
            // TO-DO ENTREGA FINAL mapView.controller.setCenter(waypoints[0])

        }
    }


    private fun checkLocationSettings() {
        // TO-DO ENTREGA FINAL val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        // TO-DO ENTREGA FINAL val client: SettingsClient = LocationServices.getSettingsClient(this)
        // TO-DO ENTREGA FINAL val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        // TO-DO ENTREGA FINAL task.addOnSuccessListener {
        // TO-DO ENTREGA FINAL     Log.i("LOCATION", "GPS is ON")
        // TO-DO ENTREGA FINAL     var settingsOK = true
        // TO-DO ENTREGA FINAL     startLocationUpdates()
        // TO-DO ENTREGA FINAL }
    }


    // TO-DO ENTREGA FINAL private fun createLocationRequest(): LocationRequest =
    // TO-DO ENTREGA FINAL     // New builder
    // TO-DO ENTREGA FINAL     LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).apply {
    // TO-DO ENTREGA FINAL         setMinUpdateIntervalMillis(5000)
    // TO-DO ENTREGA FINAL     }.build()


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // TO-DO ENTREGA FINAL mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.drawer_menu, menu)
        //Ocultar boton si el usuario no es restaurante
        menu?.findItem(R.id.miRestaurante)?.isVisible = Sesion.esRestaurante
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TO-DO ENTREGA FINAL var intentCuenta = Intent(this, Perfil::class.java)
        var intentMiRestaurante = Intent(this, MiRestaurante::class.java)
        var intentCerrarSesion = Intent(this, InicioSesion::class.java)
        when (item.itemId) {
            // TO-DO ENTREGA FINAL R.id.Cuenta -> startActivity(intentCuenta)
            R.id.miRestaurante -> startActivity(intentMiRestaurante)
            R.id.Inicio -> {}
            R.id.cerrarSesion -> {
                Funciones.clearSesion()
                intentCerrarSesion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intentCerrarSesion)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pedirPermiso(
        context: Activity,
        permiso: String,
        justificacion: String,
        idCode: Int
    ) {
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
                ActivityCompat.requestPermissions(this, arrayOf(permiso), idCode)
            }
        }
    }


    private fun setLocation() {
        // Verifica permisos antes de intentar acceder a la ubicación
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // TO-DO ENTREGA FINAL mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // TO-DO ENTREGA FINAL     if (location != null) {
            // TO-DO ENTREGA FINAL         buscarRestaurante(location)
            // TO-DO ENTREGA FINAL         showPermissionStatus(true)
            // TO-DO ENTREGA FINAL     } else {
            // TO-DO ENTREGA FINAL         statusTextView.text = "No se pudo obtener la ubicación."
            // TO-DO ENTREGA FINAL         statusTextView.setTextColor(
            // TO-DO ENTREGA FINAL             ContextCompat.getColor(
            // TO-DO ENTREGA FINAL                 this,
            // TO-DO ENTREGA FINAL                 android.R.color.holo_red_dark
            // TO-DO ENTREGA FINAL             )
            // TO-DO ENTREGA FINAL         )
            // TO-DO ENTREGA FINAL     }
            // TO-DO ENTREGA FINAL }
        } else {
            // Mostrar estado de permiso denegado
            showPermissionStatus(false)
        }
    }

    @SuppressLint("SetTextI18n")
    fun buscarRestaurante(location: Location) {

        // TO-DO ENTREGA FINAL val userLocation = GeoPoint(location.latitude, location.longitude)
        Data.latitud = location.latitude
        Data.longitud = location.longitude
        val restaurantes = Data.RESTAURANT_LIST

        botonHabilitado()

        // Eliminar los marcadores anteriores (si existen)
        // TO-DO ENTREGA FINAL for (marker in listaMarkerRest) {
        // TO-DO ENTREGA FINAL     mapView.overlays.remove(marker)
        // // TO-DO ENTREGA FINAL TO-DO ENTREGA FINAL }
        // // TO-DO ENTREGA FINAL TO-DO ENTREGA FINAL listaMarkerRest.clear() // Limpiar // TO-DO ENTREGA FINAL la lista de marcadores

        // TO-DO ENTREGA FINAL userMarker?.remove(mapView)

        // TO-DO ENTREGA FINAL mapView.setTileSource(TileSourceFactory.MAPNIK)
        // TO-DO ENTREGA FINAL mapView.setBuiltInZoomControls(true)
        // TO-DO ENTREGA FINAL mapView.setMultiTouchControls(true)

        //Ubicar el mapa en la ubicación del usuario
        // TO-DO ENTREGA FINAL mapView.controller.setZoom(15.0)
        // TO-DO ENTREGA FINAL mapView.controller.setCenter(userLocation)

        // Añadir un marcador en la ubicación del usuario
        // TO-DO ENTREGA FINAL userMarker = Marker(mapView)
        // TO-DO ENTREGA FINAL userMarker?.position = userLocation
        // TO-DO ENTREGA FINAL userMarker?.icon = crearMarcador(Color.RED)
        // TO-DO ENTREGA FINAL userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        // TO-DO ENTREGA FINAL userMarker?.title = "Tu ubicación"
        // TO-DO ENTREGA FINAL userMarker?.alpha = 1.0f
        // TO-DO ENTREGA FINAL mapView.overlays.add(userMarker)

        // TO-DO ENTREGA FINAL compassOverlay = CompassOverlay(this, mapView)
        // TO-DO ENTREGA FINAL (compassOverlay as CompassOverlay).enableCompass()
        // TO-DO ENTREGA FINAL mapView.overlays.add(compassOverlay)


        for (restaurant in restaurantes) {
            // TO-DO ENTREGA FINAL val markerRestaurante = Marker(mapView)
            // TO-DO ENTREGA FINAL val point = GeoPoint(restaurant.latitud, restaurant.longitud)
            // TO-DO ENTREGA FINAL markerRestaurante.position = point
            // TO-DO ENTREGA FINAL markerRestaurante.title = restaurant.nombre
            // TO-DO ENTREGA FINAL markerRestaurante.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            // TO-DO ENTREGA FINAL markerRestaurante.subDescription =
            // TO-DO ENTREGA FINAL     "Calificacion de " + restaurant.calificacion.toString()


            // TO-DO ENTREGA FINAL markerRestaurante.alpha =
            // TO-DO ENTREGA FINAL     if (Data.RESTAURANT_ROUTE.contains(restaurant)) 3.0f else 0.09f

            // Añadir el marcador al mapa
            // TO-DO ENTREGA FINAL mapView.overlays.add(markerRestaurante)
            // TO-DO ENTREGA FINAL markerRestaurante.showInfoWindow()
            // TO-DO ENTREGA FINAL listaMarkerRest.add(markerRestaurante)
        }

        //Refrescar el mapa
        // TO-DO ENTREGA FINAL mapView.invalidate()
    }


    private fun crearMarcador(color: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(color)
        drawable.setSize(30, 30)  // Tamaño del ícono
        return drawable
    }


    private fun getRestaurantsByProximity(location: Location, restaurantes: ArrayList<Restaurant>): List<Restaurant> {
        var userLat = location.latitude
        var userLong = location.longitude
        return restaurantes.sortedBy { restaurant ->
            Funciones.distance(userLat, userLong, restaurant.latitud, restaurant.longitud)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCode -> {
                // Si el permiso fue cancelado, el arreglo de permisos esta vacio
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // El permiso fue concedido, usar ubicacion
                    setLocation()
                } else {
                    // Mostrar estado de permiso denegado
                    showPermissionStatus(false)
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun showPermissionRationale() {
        Toast.makeText(
            this, "Servicios reducidos", Toast.LENGTH_LONG).show()
    }

    private fun showPermissionStatus(granted: Boolean) {
        if (!granted) {
            statusTextView.text = "¡PERMISO DENEGADO!"
            boton.visibility = View.GONE
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            statusTextView.text = "¡BIENVENIDO!" // Limpiar el mensaje cuando se cargan los contactos
            boton.visibility = View.VISIBLE
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
        }
    }

    override fun onResume() {
        super.onResume()
        // TO-DO ENTREGA FINAL mapView.onResume()
        // TO-DO ENTREGA FINAL val mapController: IMapController = mapView.controller
        // TO-DO ENTREGA FINAL mapController.setZoom(18.0)

        // Inicializa el callback en onResume para que se reinicie la actividad
        checkLocationSettings()

        // Intentar obtener la ubicación y centrar el mapa en la reanudación de la actividad
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setLocation()
        }

    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        // TO-DO ENTREGA FINAL mapView.onPause()
    }

    private fun stopLocationUpdates() {
        // TO-DO ENTREGA FINAL mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }




}

