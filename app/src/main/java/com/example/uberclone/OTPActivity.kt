package com.example.uberclone

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uberclone.databinding.ActivityOtpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTPActivity: AppCompatActivity(){
    private lateinit var binding: ActivityOtpBinding
    private val phoneNumber by lazy { intent.getStringExtra("phone") }
    var verificationCodeBySystem: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)

        setContentView(binding.root)
        phoneNumber?.let { sendVerificationCode(it) }

        binding.verify.setOnClickListener {
            val _OTP = binding.otp.text.toString()
            if (_OTP.isEmpty() || _OTP.length < 6) {
                binding.TILotp.setError(" ")
                binding.TILotp.boxStrokeErrorColor = ColorStateList.valueOf(Color.parseColor("#FF0000"))
                binding.otp.requestFocus()
                return@setOnClickListener
            }
            verifyCode(_OTP)
        }
        binding.dismiss.setOnClickListener {
            binding.otp.text?.clear()
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val options: PhoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91$phoneNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationCodeBySystem = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code: String? = phoneAuthCredential.getSmsCode()
                if (code != null) verifyCode(code)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("Error", e.message!!)
                val intent = Intent(
                    applicationContext,
                    LoginActivity::class.java
                )
                startActivity(intent)
                Toast.makeText(this@OTPActivity, e.message + "Please try again", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun verifyCode(codeByUser: String) {
        val credential: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser)
        signInUser(credential)
    }

    private fun signInUser(credential: PhoneAuthCredential) {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(
                        applicationContext,
                        MainActivity::class.java
                    )
                    intent.putExtra("PhnNo", phoneNumber)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
//                    val intent = Intent(
//                        applicationContext,
//                        LoginActivity::class.java
//                    )
//                    startActivity(intent)
                    binding.otp.text?.clear()
                    Toast.makeText(
                        this@OTPActivity,
                        task.exception!!.message + "Please try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}