package com.example.letsGoApp.views.usuario

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.letsGoApp.R
import com.example.letsGoApp.models.usuario

class UsuarioAdapter(private val context: Context, private val usuarios: List<usuario>) : BaseAdapter() {

    override fun getCount(): Int {
        return usuarios.size
    }

    override fun getItem(position: Int): Any {
        return usuarios[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
        } else {
            view = convertView
        }

        val textViewNome = view.findViewById<TextView>(R.id.textViewNome)

        val usuario = usuarios[position]
        textViewNome.text = usuario.nome

        return view
    }
}
