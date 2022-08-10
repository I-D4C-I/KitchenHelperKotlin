package com.example.kitchenhelperkotlin.tobuy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kitchenhelperkotlin.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ToBuyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tobuy)
    }
}