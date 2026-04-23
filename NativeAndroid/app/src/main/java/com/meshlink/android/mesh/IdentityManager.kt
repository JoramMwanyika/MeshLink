package com.meshlink.android.mesh

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class IdentityManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("meshlink_identity", Context.MODE_PRIVATE)

    fun getDeviceId(): String {
        var id = prefs.getString("device_id", null)
        if (id == null) {
            id = "ML-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
            prefs.edit().putString("device_id", id).apply()
        }
        return id
    }

    fun getUsername(): String? {
        return prefs.getString("username", null)
    }

    fun setUsername(name: String) {
        prefs.edit().putString("username", name).apply()
    }

    fun getUserColor(): Int {
        return prefs.getInt("user_color", 0xFF00FF66.toInt())
    }

    fun setUserColor(color: Int) {
        prefs.edit().putInt("user_color", color).apply()
    }

    fun isSetupComplete(): Boolean {
        return getUsername() != null
    }
}
