package com.example.letsgoapp.ui.usuario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letsgoapp.R
import com.example.letsgoapp.databinding.FragmentLoginUsuarioBinding

class LoginUsuarioFragment : Fragment() {

    private var _binding: FragmentLoginUsuarioBinding? = null
    private lateinit var cadastrarLabel : TextView

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginUsuarioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cadastrarLabel = root.findViewById(R.id.inscreverLabel)
        cadastrarLabel.setOnClickListener{
            findNavController().navigate(R.id.action_login_to_cadastro_usuario)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}