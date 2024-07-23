package com.example.letsGoApp.views.usuario

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letsGoApp.R
import com.example.letsGoApp.interfaces.UsuariosService
import com.example.letsGoApp.models.usuario
import com.example.letsGoApp.controllers.Utils
import com.example.letsGoApp.controllers.Utils.Companion.toSHA256
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class cadastroUsuarioFragment : Fragment() {

    private lateinit var nomeInput: EditText
    private lateinit var sobrenomeInput: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var dataNascimentoInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var senhaInput: EditText
    private lateinit var cadastrarButton: Button
    private lateinit var usuariosService: UsuariosService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cadastro_usuario, container, false)

        nomeInput = view.findViewById(R.id.nomeCadastroUsuarioInput)
        sobrenomeInput = view.findViewById(R.id.sobrenomeCadastroUsuarioInput)
        radioGroup = view.findViewById(R.id.sexRadioGroup)
        dataNascimentoInput = view.findViewById(R.id.dataNascimentoUsuarioCadastroInput)
        emailInput = view.findViewById(R.id.editTextTextEmailAddress)
        senhaInput = view.findViewById(R.id.senhaUsuarioCadastroInput)
        cadastrarButton = view.findViewById(R.id.buttonCadastroUsuario)
        usuariosService = Utils.getRetrofitInstance(Utils.getPathString()).create(UsuariosService::class.java)

        dataNascimentoInput.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "##/##/####"
            private var old = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val str = unmask(charSequence.toString())
                var formatted = ""
                if (isUpdating) {
                    old = str
                    isUpdating = false
                    return
                }
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > old.length) {
                        formatted += m
                        continue
                    }
                    try {
                        formatted += str[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }
                isUpdating = true
                dataNascimentoInput.setText(formatted)
                dataNascimentoInput.setSelection(formatted.length)
            }

            override fun afterTextChanged(editable: Editable) {}

            private fun unmask(s: String): String {
                return s.replace("[^\\d]".toRegex(), "")
            }
        })

        cadastrarButton.setOnClickListener {
            cadastrarUsuario()
            findNavController().navigate(R.id.navigation_mapa)
        }

        return view
    }

    private fun cadastrarUsuario() {
        val nome = nomeInput.text.toString().trim()
        val sobrenome = sobrenomeInput.text.toString().trim()
        val sexoId = radioGroup.checkedRadioButtonId
        val sexo = view?.findViewById<RadioButton>(sexoId)?.text.toString()
        val dataNascimento = dataNascimentoInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val senha = senhaInput.text.toString().trim()

        if (nome.isEmpty() || sobrenome.isEmpty() || dataNascimento.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(context, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Utils.isEmailValid(email)) {
            Toast.makeText(context, "Por favor, insira um email válido", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val dataNascimentoFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataNascimento)
            val dataNascimentoFormattedString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dataNascimentoFormatted)
            val senhaHashed = senha.toSHA256()
            val usuario = usuario(nome, sobrenome, sexo, dataNascimentoFormattedString, email, senhaHashed)

            usuariosService.createUsuario(usuario).enqueue(object : Callback<usuario> {
                override fun onResponse(call: Call<usuario>, response: Response<usuario>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("API_ERROR", "${dataNascimentoFormattedString}")
                        Log.d("API_ERROR", "Erro ao cadastrar usuário: $errorBody")
                        Log.d("API_ERROR", "Response: ${response.raw()}")
                        Toast.makeText(context, "Erro ao cadastrar usuário: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<usuario>, t: Throwable) {
                    Log.e("API_ERROR", "Falha na conexão: ${t.message}", t)
                    Toast.makeText(context, "Falha na conexão: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } catch (e: ParseException) {
            Toast.makeText(context, "Data de nascimento inválida", Toast.LENGTH_SHORT).show()
        }
    }

}
