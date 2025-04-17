package moiz.dev.mainapp.utils

import android.content.Context

fun savePin(context: Context, pin: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("user_pin", pin).apply()
}

fun getSavedPin(context: Context): String {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("user_pin", "1234") ?: "1234" // default PIN new 0000
}