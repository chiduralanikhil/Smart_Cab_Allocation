package com.example.uberclone

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {
    private const val BASE_URL = "https://maps.googleapis.com/"

    val api: Service by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())  // Add coroutine adapter
            .build()
            .create(Service::class.java)
    }
}
