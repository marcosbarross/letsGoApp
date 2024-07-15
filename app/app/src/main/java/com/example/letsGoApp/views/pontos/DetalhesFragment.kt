package com.example.letsGoApp.views.pontos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.letsGoApp.databinding.FragmentDetalhesBinding
import com.example.letsGoApp.models.PontoOrdenado

class DetalhesFragment : Fragment() {

    private var _binding: FragmentDetalhesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalhesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val ponto: PontoOrdenado? = arguments?.getParcelable("ponto")
        ponto?.let {
            binding.nomeAtividadeDetalhes.text = it.nome
            binding.infoTextView.text = "${Math.round(it.distancia)} km, ${it.atividade}"
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(ponto: PontoOrdenado): DetalhesFragment {
            val fragment = DetalhesFragment()
            val args = Bundle()
            args.putParcelable("ponto", ponto)
            fragment.arguments = args
            return fragment
        }
    }
}