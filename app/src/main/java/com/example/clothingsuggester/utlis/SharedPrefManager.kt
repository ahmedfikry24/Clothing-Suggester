package com.example.clothingsuggester.utlis

import android.content.Context
import android.content.SharedPreferences

object SharedPrefManager {
    private var sharedPreference: SharedPreferences? = null

    fun getInit(context: Context): SharedPreferences {
        sharedPreference =
            context.getSharedPreferences(Constant.SHARED_PREFERENCE_Name, Context.MODE_PRIVATE)
        return sharedPreference!!
    }

    var imageNumber: Int?
        get() = sharedPreference?.getInt(Constant.KEY_IMAGE_NUMBER, 0)
        set(value) {
            sharedPreference?.edit()?.putInt(Constant.KEY_IMAGE_NUMBER, value!!)?.apply()
        }
    var isSelectedImage: Boolean?
        get() = sharedPreference?.getBoolean(Constant.KEY_IS_SELECTED_IMAGE, false)
        set(value) {
            sharedPreference?.edit()?.putBoolean(Constant.KEY_IS_SELECTED_IMAGE, value!!)?.apply()
        }
}