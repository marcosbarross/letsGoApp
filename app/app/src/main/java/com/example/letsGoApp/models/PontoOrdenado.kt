package com.example.letsGoApp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PontoOrdenado(
    @SerializedName("id") var id: Int,
    @SerializedName("nome") val nome: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("distancia") val distancia: Double,
    @SerializedName("atividade") val atividade: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("dias_semana") val diasSemana: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nome)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeDouble(distancia)
        parcel.writeString(atividade)
        parcel.writeString(horario)
        parcel.writeString(diasSemana)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PontoOrdenado> {
        override fun createFromParcel(parcel: Parcel): PontoOrdenado {
            return PontoOrdenado(parcel)
        }

        override fun newArray(size: Int): Array<PontoOrdenado?> {
            return arrayOfNulls(size)
        }
    }
}
