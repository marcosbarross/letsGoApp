package com.example.letsGoApp.controllers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.letsGoApp.models.PontoOrdenado
import com.example.letsGoApp.databinding.ItemEncontroBinding

class PontosAdapter(
    private val pontos: List<PontoOrdenado>,
    private val locationController: LocationController
) : RecyclerView.Adapter<PontosAdapter.PontoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PontoViewHolder {
        val binding = ItemEncontroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PontoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PontoViewHolder, position: Int) {
        val ponto = pontos[position]
        holder.bind(ponto)
    }

    override fun getItemCount(): Int {
        return pontos.size
    }

    inner class PontoViewHolder(private val binding: ItemEncontroBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ponto: PontoOrdenado) {
            binding.nomeTextView.text = ponto.nome
            binding.infoTextView.text = "${ponto.distancia} km"
        }
    }
}
