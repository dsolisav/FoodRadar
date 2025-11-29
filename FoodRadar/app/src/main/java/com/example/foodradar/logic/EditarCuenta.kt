package com.example.foodradar.logic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodradar.R
import com.example.foodradar.data.Funciones
import com.example.foodradar.data.Sesion
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class EditarCuenta : AppCompatActivity() {
    lateinit var editNombre: EditText
    lateinit var editApellido: EditText
    lateinit var editUsername: EditText
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_cuenta)
        editNombre = findViewById<EditText>(R.id.editNombre)
        editApellido = findViewById<EditText>(R.id.editApellido)
        editUsername = findViewById<EditText>(R.id.editUsername)
        var botonEditarCuenta = findViewById<Button>(R.id.botonEditarCuenta)
        botonEditarCuenta.setOnClickListener{editarCuenta()}
        editNombre.setText(Sesion.nombre)
        editApellido.setText(Sesion.apellido)
        editUsername.setText(Sesion.userName)
    }

    fun editarCuenta(){
        val collectionRef = db.collection("usuarios")
        val documentId = Sesion.userId
        val updates = mapOf(
            "nombre" to editNombre.text.toString(),
            "apellido" to editApellido.text.toString(),
            "userName" to editUsername.text.toString()
        )
        collectionRef.document(documentId).update(updates)
            .addOnSuccessListener {
                Log.i("UPDATE-DOCUMENT", "Document with ID $documentId successfully updated.")
                Sesion.nombre = editNombre.text.toString()
                Sesion.apellido = editApellido.text.toString()
                Sesion.userName = editUsername.text.toString()
                intent = Intent(this, Perfil::class.java)
                startActivity(intent)

            }
            .addOnFailureListener { exception ->
                Log.e("UPDATE-DOCUMENT", "Error updating document", exception)
            }
    }
}
