package com.example.letsGoApp.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letsGoApp.models.pontosOrdenados
import com.example.letsGoApp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EstacionamentoAdapter(
    private val estacionamentos: List<pontosOrdenados>,
    private val locationController: LocationController
) :
    RecyclerView.Adapter<EstacionamentoAdapter.EstacionamentoViewHolder>() {

    inner class EstacionamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeTextView: TextView = itemView.findViewById(R.id.nomeTextView)
        val infoTextView: TextView = itemView.findViewById(R.id.infoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstacionamentoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_encontro, parent, false)
        return EstacionamentoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EstacionamentoViewHolder, position: Int) {
        val estacionamento = estacionamentos[position]
        CoroutineScope(Dispatchers.IO).launch {
            val latitude = withContext(Dispatchers.Main) {
                locationController.getLatitude()
            }
            val longitude = withContext(Dispatchers.Main) {
                locationController.getLongitude()
            }
            withContext(Dispatchers.Main) {
                holder.nomeTextView.text = estacionamento.nome
                holder.infoTextView.text = "${estacionamento.distancia_km} km, R$ ${estacionamento.preco.toString()}"
            }
        }
    }

    override fun getItemCount(): Int {
        return estacionamentos.size
    }
}