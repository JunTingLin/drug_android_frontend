package com.junting.drug_android_frontend.libs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.Toast

class SharedPreferencesManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun saveGoogleIdToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("GOOGLE_ID_TOKEN", token)
        editor.apply()
    }

    fun getGoogleIdToken(): String? {
        return sharedPreferences.getString("GOOGLE_ID_TOKEN", null)
    }

    fun clearGoogleIdToken() {
        val editor = sharedPreferences.edit()
        editor.remove("GOOGLE_ID_TOKEN")
        editor.apply()
    }
    fun saveUserEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("USER_EMAIL", email)
        editor.apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("USER_EMAIL", null)
    }

    fun clearUserEmail() {
        val editor = sharedPreferences.edit()
        editor.remove("USER_EMAIL")
        editor.apply()
    }

    fun saveUserName(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString("USER_NAME", name)
        editor.apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("USER_NAME", null)
    }

    fun clearUserName() {
        val editor = sharedPreferences.edit()
        editor.remove("USER_NAME")
        editor.apply()
    }

    fun savePictureUrl(url: String) {
        val editor = sharedPreferences.edit()
        editor.putString("PICTURE_URL", url)
        editor.apply()
    }

    fun getPictureUrl(): String? {
        return sharedPreferences.getString("PICTURE_URL", null)
    }

    fun clearPictureUrl() {
        val editor = sharedPreferences.edit()
        editor.remove("PICTURE_URL")
        editor.apply()
    }

    fun saveOrUpdateDrugNames(drugNames: Set<String>){
        val editor = sharedPreferences.edit()
        editor.putStringSet("DRUG_NAMES", drugNames)
        editor.apply()
    }
    fun getDrugNames(): Set<String>? {
        return sharedPreferences.getStringSet("DRUG_NAMES", null)
    }
    fun setFakeFlag(flag: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("FAKE_FLAG", flag)
        editor.apply()
    }
    fun getFakeFlag(): Boolean {
        return sharedPreferences.getBoolean("FAKE_FLAG", false)
    }
    fun toggleFakeFlag() {
        val currentFlag = getFakeFlag()
        setFakeFlag(!currentFlag)
        Toast.makeText(context, "Fake Flag: ${!currentFlag}", Toast.LENGTH_SHORT).show()
    }
}