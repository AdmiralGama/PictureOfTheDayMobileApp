package com.coslund.lazycalc

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val aPoD : APoD = findViewById(R.id.APoD)

        aPoD.titleText = findViewById(R.id.titleText)
        aPoD.descriptionText = findViewById(R.id.descriptionText)
        aPoD.copyrightText = findViewById(R.id.copyrightText)

        aPoD.imageView = findViewById(R.id.imageView)
        aPoD.dateButton = findViewById(R.id.dateButton)
        aPoD.dateText = findViewById(R.id.dateText)
    }
}