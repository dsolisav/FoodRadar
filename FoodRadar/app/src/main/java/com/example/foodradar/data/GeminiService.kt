package com.example.foodradar.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiService {
    companion object {
        private const val API_KEY = "***REMOVED***"
        private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$API_KEY"

        suspend fun getRecomendacion(prompt: String): String {
            return withContext(Dispatchers.IO) {
                try {
                    val url = URL(API_URL)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true

                    val requestBody = JSONObject().apply {
                        put("contents", JSONArray().apply {
                            put(JSONObject().apply {
                                put("parts", JSONArray().apply {
                                    put(JSONObject().apply {
                                        put("text", prompt)
                                    })
                                })
                            })
                        })
                    }

                    val writer = OutputStreamWriter(connection.outputStream)
                    writer.write(requestBody.toString())
                    writer.flush()
                    writer.close()

                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val reader = BufferedReader(InputStreamReader(connection.inputStream))
                        val response = reader.readText()
                        reader.close()

                        // Parsear la respuesta
                        val jsonResponse = JSONObject(response)
                        val candidates = jsonResponse.getJSONArray("candidates")
                        val content = candidates.getJSONObject(0).getJSONObject("content")
                        val parts = content.getJSONArray("parts")
                        val text = parts.getJSONObject(0).getString("text")
                        
                        text
                    } else {
                        val errorReader = BufferedReader(InputStreamReader(connection.errorStream))
                        val errorResponse = errorReader.readText()
                        errorReader.close()
                        Log.e("GeminiService", "Error: $responseCode - $errorResponse")
                        "Error al obtener la recomendación. Intenta de nuevo."
                    }
                } catch (e: Exception) {
                    Log.e("GeminiService", "Exception: ${e.message}")
                    "Error de conexión. Verifica tu internet."
                }
            }
        }

        fun generarPromptRecomendacion(): String {
            val categoria = Sesion.restaurantMode
            val restaurantes = Data.RESTAURANT_LIST

            if (restaurantes.isEmpty()) {
                return ""
            }

            val listaFormateada = restaurantes.map { rest ->
                val distancia = Funciones.distance(
                    Data.latitud ?: 0.0, Data.longitud ?: 0.0,
                    rest.latitud, rest.longitud
                )
                "- ${rest.nombre}: Calificación ${rest.calificacion}/5, a $distancia km de distancia"
            }.joinToString("\n")

            return """
                Eres un asistente amigable de recomendación de restaurantes llamado FoodRadar AI.
                El usuario está buscando restaurantes de tipo: $categoria
                
                Restaurantes disponibles cerca del usuario:
                $listaFormateada
                
                Basándote en la calificación y la distancia, recomienda cuál restaurante debería visitar el usuario.
                Responde en español, de forma breve y amigable (máximo 3 oraciones).
                Menciona el nombre del restaurante recomendado y por qué lo recomiendas.
            """.trimIndent()
        }
    }
}
