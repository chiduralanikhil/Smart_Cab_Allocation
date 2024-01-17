package com.example.uberclone

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {
        @GET("/maps/api/directions/json")
        fun getDirections(
            @Query("mode") mode:String,
            @Query("transit_routing_preference") preference:String,
            @Query("origin") origin:String,
            @Query("destination") destination:String,
            @Query("key") key:String,
        ) : Call<DirectionResponse>
}