package com.example.letsGoApp.views.usuario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letsGoApp.R
import com.example.letsGoApp.controllers.Utils
import com.example.letsGoApp.models.usuario

class UsuarioAdapter(private val usuarios: List<usuario>) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNome: TextView = view.findViewById(R.id.textViewNome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.textViewNome.text = Utils.capitalize(usuario.nome)
    }

    override fun getItemCount(): Int {
        return usuarios.size
    }
}
