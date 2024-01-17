package com.example.uberclone

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uberclone.databinding.ActivityLoginBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

class LoginActivity :AppCompatActivity(){
    private lateinit var binding: ActivityLoginBinding
    private val LOCATION_PERMISSION_CODE = 2
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        locationRequest.setInterval(5000)
        locationRequest.setFastestInterval(2000)

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(applicationContext)
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                Toast.makeText(this@LoginActivity, "GPS is On", Toast.LENGTH_SHORT).show()
                binding.signup.isEnabled = true
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvableApiException = e as ResolvableApiException
                        try {
                            resolvableApiException.startResolutionForResult(
                                this@LoginActivity,
                                LOCATION_PERMISSION_CODE
                            )
                        } catch (ex: SendIntentException) {
                            ex.printStackTrace()
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }

        binding.signup.setOnClickListener {
            val phone = binding.phone.getText().toString()
            if(phone.length < 10) {
                binding.TILphone.error = " "
                binding.TILphone.boxStrokeErrorColor = ColorStateList.valueOf(Color.parseColor("#FF0000"))
                binding.TILphone.requestFocus()
                return@setOnClickListener
            }
            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            intent.putExtra("phone", binding.phone.text.toString())
            startActivity(intent)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    binding.signup.isEnabled = true
                }

                RESULT_CANCELED -> {
                    binding.signup.isEnabled = false
                    Toast.makeText(this, "GPS is required to be turned on", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}