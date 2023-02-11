package com.sumberrejeki.absensi.data.sharedpreferences

import android.content.Context
import com.sumberrejeki.absensi.data.model.Pegawai

internal class UserPreference(context: Context) {
    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val NIP = "nip"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val SALARY = "salary"
        private const val PHONE_NUMBER = "phone"
        private const val POSITION = "position"
    }
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    fun setUser(value: Pegawai) {
        val editor = preferences.edit()
        editor.putString(NIP, value.nIP)
        editor.putString(NAME, value.nama)
        editor.putString(EMAIL, value.email)
        editor.putInt(SALARY, value.gajiPokok.toInt())
        editor.putString(PHONE_NUMBER, value.noTelp)
        editor.putString(POSITION, value.jabatan)
        editor.apply()
    }
    fun getUser(): Pegawai {
        val model = Pegawai()
        model.nIP = preferences.getString(NIP, "").toString()
        model.nama = preferences.getString(NAME, "").toString()
        model.email = preferences.getString(EMAIL, "").toString()
        model.gajiPokok = preferences.getInt(SALARY, 0).toString()
        model.noTelp = preferences.getString(PHONE_NUMBER, "").toString()
        model.jabatan = preferences.getString(POSITION, "").toString()
        return model
    }

    fun logout() {
        val editor = preferences.edit()
        editor.putString(NIP, "")
        editor.putString(NAME, "")
        editor.putString(EMAIL, "")
        editor.putInt(SALARY, 0)
        editor.putString(PHONE_NUMBER, "")
        editor.putString(POSITION, "")
        editor.apply()
    }
}