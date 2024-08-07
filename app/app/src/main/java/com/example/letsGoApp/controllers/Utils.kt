package com.example.letsGoApp.controllers

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import android.util.Patterns

class Utils {
    companion object {
        val path = "https://letsgoapp.onrender.com"

        fun getRetrofitInstance(path: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(path)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getPathString(): String {
            return path
        }

        fun String.toSHA256(): String {
            val bytes = this.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("", { str, it -> str + "%02x".format(it) })
        }

        fun isEmailValid(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun capitalize(string: String): String {
             val capitalized = string.substring(0, 1).uppercase() + string.substring(1)
            return capitalized
        }
    }
}
