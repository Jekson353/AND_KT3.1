package com.samoylenko.kt12.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.samoylenko.kt12.R
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MainFragmentActivity : AppCompatActivity(R.layout.activity_main_fragment) {

    object StringArg: ReadWriteProperty<Bundle, String?> {
        override fun setValue(thisRef: Bundle, property: KProperty<*>, value: String?) {
            thisRef.putString(property.name, value)
        }
        override fun getValue(thisRef: Bundle, property: KProperty<*>): String? =
            thisRef.getString(property.name)
    }

}