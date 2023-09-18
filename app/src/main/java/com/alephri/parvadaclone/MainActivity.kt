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
    lateinit var notificationByIdButton: Button
    lateinit var notificationsButton: Button

    private val alephSDK = AlephSDK(this, apiKey = "RFh4DsMud6PsT7VHArgPYRRH")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        riskRouteButton = findViewById(R.id.riskRouteButton)
        loginButton = findViewById(R.id.loginBtn)
        notificationByIdButton = findViewById(R.id.notificationByIdBtn)
        notificationsButton = findViewById(R.id.notificationsBtn)
    }

    override fun onResume() {
        super.onResume()
        loginButton.setOnClickListener {
            MainScope().launch {
                alephSDK.login(userName = "OFRH")
            }
        }
        riskRouteButton.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }
        notificationByIdButton.setOnClickListener {
            MainScope().launch {
                alephSDK.getNotificationById(239764).collect {
                    when (it) {
                        is State.Success -> {
                            Log.d("Success", "Notification ${it.data}")
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
        notificationsButton.setOnClickListener {
            MainScope().launch {
                alephSDK.getNotifications(10, offset = 0).collect {
                    when (it) {
                        is State.Success -> {
                            Log.d("Success", "Notifications ${it.data.size}")
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
    }
}