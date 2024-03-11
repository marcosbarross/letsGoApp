package com.example.letsgoapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.letsgoapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Aqui, use binding.titulo para acessar o TextView com o ID "titulo"
        val tituloTextView = binding.titulo
        // Você pode fazer o que quiser com o TextView aqui

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
