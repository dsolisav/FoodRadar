package com.example.foodradar.logic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodradar.R
import com.example.foodradar.data.Sesion

class CambiarContrasena : AppCompatActivity() {
    lateinit var editNuevaContrasena: EditText
    lateinit var editConfirmarContrasena: EditText
    lateinit var botonGuardarCambios: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_contrasena)
        editNuevaContrasena = findViewById<EditText>(R.id.editNuevaContrasena)
        editConfirmarContrasena = findViewById<EditText>(R.id.editConfirmarContrasena)
        botonGuardarCambios = findViewById<Button>(R.id.botonGuardarCambios)
        botonGuardarCambios.setOnClickListener{cambiarContrasena()}

    }

    fun cambiarContrasena(){
        val user = Sesion.auth.currentUser
        val nuevaContrasena = editNuevaContrasena.text.toString()
        val confirmarContrasena = editConfirmarContrasena.text.toString()
        if(nuevaContrasena == confirmarContrasena){
            user?.let {
                user.updatePassword(confirmarContrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("UPDATE-PASSWORD", "Password updated successfully.")
                            Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                            intent = Intent(this, Perfil::class.java)
                            startActivity(intent)
                        } else {
                            Log.e("UPDATE-PASSWORD", "Error updating password", task.exception)
                            Toast.makeText(this, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show()
                        }
                    }
            } ?: run {
                Log.e("UPDATE-PASSWORD", "No user is signed in.")
                Toast.makeText(this, "Usuario no logeado", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }

    }
}
