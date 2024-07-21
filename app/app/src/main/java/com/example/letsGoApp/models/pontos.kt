package com.example.letsGoApp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class pontos(
    @SerializedName("id_usuario" )val id_usuario: Int,
    @SerializedName("id_ponto" )val id_ponto: Int,
    @SerializedName("atividade") val atividade: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("dias_semana") val diasSemana: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("distancia") val distancia: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_usuario)
        parcel.writeInt(id_ponto)
        parcel.writeString(atividade)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(diasSemana)
        parcel.writeString(horario)
        parcel.writeDouble(distancia)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<pontos> {
        override fun createFromParcel(parcel: Parcel): pontos {
            return pontos(parcel)
        }

        override fun newArray(size: Int): Array<pontos?> {
            return arrayOfNulls(size)
        }
    }
}
