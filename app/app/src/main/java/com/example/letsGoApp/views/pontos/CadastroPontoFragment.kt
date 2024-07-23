package com.example.letsGoApp.views.pontos

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.letsGoApp.R
import com.example.letsGoApp.controllers.Utils
import com.example.letsGoApp.databinding.FragmentCadastroPontoBinding
import com.example.letsGoApp.interfaces.PontosService
import com.example.letsGoApp.models.PontoCreate
import com.example.letsGoApp.views.MapaFragment
import com.example.letsGoApp.views.usuario.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class CadastroPontoFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentCadastroPontoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCadastroPontoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextHorario.setOnClickListener {
            showTimePickerDialog()
        }

        binding.buttonSave.setOnClickListener {
            savePonto()
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.editTextHorario.setText(formattedTime)
            }, hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun savePonto() {
        val atividade = binding.editTextAtividade.text.toString()
        val horario = binding.editTextHorario.text.toString()
        val latitude = MapaFragment.latitude
        val longitude = MapaFragment.longitude
        val diasSemana = getSelectedDiasSemana()
        val userId = sharedViewModel.getUserId()

        if (atividade.isEmpty() || horario.isEmpty() || latitude == null || longitude == null || diasSemana.isEmpty() || userId == null) {
            Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val ponto = PontoCreate(
            idUsuario = userId,
            latitude = latitude,
            longitude = longitude,
            atividade = atividade,
            horario = horario,
            diasSemana = diasSemana.joinToString(",")
        )

        val retrofit = Utils.getRetrofitInstance(Utils.getPathString())
        val pontosService = retrofit.create(PontosService::class.java)
        val call = pontosService.createPonto(ponto)

        call.enqueue(object : Callback<PontoCreate> {
            override fun onResponse(call: Call<PontoCreate>, response: Response<PontoCreate>) =
                if (response.isSuccessful) {
                    Toast.makeText(context, "Ponto criado com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_to_mapa)
                } else {
                    Toast.makeText(context, "Falha ao criar ponto", Toast.LENGTH_SHORT).show()
                }

            override fun onFailure(call: Call<PontoCreate>, t: Throwable) {
                Toast.makeText(context, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSelectedDiasSemana(): List<String> {
        val diasSemana = mutableListOf<String>()
        if (binding.checkBoxSegunda.isChecked) diasSemana.add("Segunda-feira")
        if (binding.checkBoxTerca.isChecked) diasSemana.add("Terça-feira")
        if (binding.checkBoxQuarta.isChecked) diasSemana.add("Quarta-feira")
        if (binding.checkBoxQuinta.isChecked) diasSemana.add("Quinta-feira")
        if (binding.checkBoxSexta.isChecked) diasSemana.add("Sexta-feira")
        if (binding.checkBoxSabado.isChecked) diasSemana.add("Sábado")
        if (binding.checkBoxDomingo.isChecked) diasSemana.add("Domingo")
        return diasSemana
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
