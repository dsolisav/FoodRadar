package com.example.foodradar.logic

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodradar.R
import com.example.foodradar.data.Comentario
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Sesion
import com.example.foodradar.data.Sesion.Companion.imagesRef
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CalificarRestaurante : AppCompatActivity() {

    private var imageBitmap: Bitmap? = null
    private val CAMERA_PERMISSION_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val GALLERY_REQUEST_CODE = 103

    private lateinit var imageView: ImageView
    private lateinit var btnCamara: ImageButton
    private lateinit var btnGaleria: ImageButton
    lateinit var botonCalificar : Button
    lateinit var ratingBar : RatingBar
    lateinit var editComentario : EditText

    lateinit var restaurantId: String
    lateinit var restaurantName: String
    var calificacion = 0.0
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calificar_restaurante)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        imageView = findViewById(R.id.imageView) // Cambié de TextView a ImageView
        btnCamara = findViewById(R.id.botonCamara)
        btnGaleria = findViewById(R.id.botonAddImage)
        botonCalificar = findViewById(R.id.botonCalificar)
        ratingBar = findViewById(R.id.ratingBar)
        editComentario = findViewById(R.id.editComentario)
        botonCalificar.setOnClickListener{calificar()}


        restaurantName = intent.getStringExtra("restaurantName").toString()
        calificacion = intent.getDoubleExtra("puntaje",0.0)
        restaurantId = intent.getStringExtra("restaurantId").toString()
        Log.d("DesdeCalificar", "restaurantName: $restaurantName")
        Log.d("DesdeCalificar", "calificacion: $calificacion")


        btnCamara.setOnClickListener {
            checkCameraPermission()
        }

        btnGaleria.setOnClickListener {
            openGallery()
        }
    }

    fun calificar(){
        if(bitmap == null){
            Toast.makeText(this, "Debes adjuntar una imagen", Toast.LENGTH_SHORT).show()
            return
        }
        if(ratingBar.rating == 0.0f || editComentario.text.isEmpty()){
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        var calificacion = ratingBar.rating
        var contenidoComentario = editComentario.text.toString()
        var nombreCompleto = Sesion.nombre + " " + Sesion.apellido
        var fechaActual = LocalDateTime.now()
        var formato = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss")
        var fechaComentario = fechaActual.format(formato)

        Toast.makeText(this, "Creando tu comentario...", Toast.LENGTH_LONG).show()

        val db = Firebase.firestore
        val comentariosRef = db.collection("restaurantes").document(restaurantId).collection("comentarios")

        uploadImage(bitmap!!) { imageUrl ->
            val comentario = mapOf(
                "nombre_completo" to nombreCompleto,
                "calificacion" to calificacion,
                "fecha" to fechaComentario,
                "descripcion" to contenidoComentario,
                "imageUrl" to imageUrl
            )

            comentariosRef.add(comentario)
                .addOnSuccessListener {
                    Log.d("ADD-COMENTARIO", "Comentario added successfully to restaurante $restaurantId")
                }
                .addOnFailureListener { exception ->
                    Log.w("ADD-COMENTARIO", "Error adding comentario", exception)
                }
            finish()
        }
    }

    // Verificar permisos de cámara
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    // Iniciar la cámara
    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    // Abrir galería
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    // Manejar el resultado de la cámara y galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    bitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(bitmap)
                    imageBitmap = bitmap // Guardar la imagen

                }
                GALLERY_REQUEST_CODE -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val inputStream = contentResolver.openInputStream(it)
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        imageView.setImageBitmap(bitmap)
                        imageBitmap = bitmap // Guardar la imagen

                    }
                }
            }
        }
    }

    fun uploadImage(bitmap: Bitmap, callback: (String) -> Unit) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        val imageName = "image_${System.currentTimeMillis()}.png"
        val imageRef = imagesRef.child(imageName)

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                Log.d("FirebaseUploadImage", "Image retrieved successfully")
                callback(imageUrl) // Pass the imageUrl to the callback
            }.addOnFailureListener { exception ->
                Log.e("FirebaseUploadImage", "Failed to retrieve image URL", exception)
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseUploadImage", "Failed to upload image", exception)
        }
    }

    // Manejo de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
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
