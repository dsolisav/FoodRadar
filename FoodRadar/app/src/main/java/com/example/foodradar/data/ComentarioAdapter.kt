import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodradar.data.Comentario
import com.example.foodradar.R

class ComentariosAdapter(private val context: Context, private val comentarios: List<Comentario>) :
    RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_comentario_adapter, parent, false)
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {

        val comentario = comentarios[position]
        holder.nombreComentario.text = comentario.nombre_completo
        holder.calificacionComentario.text = comentario.calificacion + " estrellas"
        holder.fechaComentario.text = comentario.fecha
        holder.contenidoComentario.text = comentario.descripcion
        Glide.with(context).load(comentario.imageUrl).into(holder.imagenComentario)
    }

    override fun getItemCount(): Int = comentarios.size

    class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreComentario: TextView = itemView.findViewById(R.id.nombreComentario)
        val calificacionComentario: TextView = itemView.findViewById(R.id.calificacionComentario)
        val fechaComentario: TextView = itemView.findViewById(R.id.fechaComentario)
        val contenidoComentario: TextView = itemView.findViewById(R.id.contenidoComentario)
        val imagenComentario: ImageView = itemView.findViewById(R.id.imagenComentario)
    }
}
