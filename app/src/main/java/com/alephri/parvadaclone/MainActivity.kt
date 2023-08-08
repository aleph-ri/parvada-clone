package com.alephri.parvadaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.alephri.sdk.data.base.State
import com.alephri.sdk.facade.AlephSDK
import com.aleprhi.sdk.type.GeoPointInput
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var riskRouteButton: Button
    lateinit var loginButton: Button
    private val alephSDK = AlephSDK(this, apiKey = "1010101010101")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        riskRouteButton = findViewById(R.id.riskRouteButton)
        loginButton = findViewById(R.id.loginBtn)
    }

    override fun onResume() {
        super.onResume()
        loginButton.setOnClickListener {
            MainScope().launch {
                alephSDK.login(email = "ofernandez@alephri.com", password = "password").collect {
                    when (it) {
                        is State.Success -> {
                            Log.d("Success", "Token ${it.data}")
                        }
                        is State.Failure -> {
                            Log.d("Failure", "Error ${it.exception}")
                        }
                        is State.Progress -> {
                            Log.d("Progress", "Is Loading")
                        }
                    }
                }
            }
        }
        riskRouteButton.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }
    }
}