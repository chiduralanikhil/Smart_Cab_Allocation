package com.example.uberclone

import com.google.gson.annotations.SerializedName

data class DirectionResponse(
    @SerializedName("routes") val routes: List<Route>?,
    @SerializedName("status") val status: String?
)

data class Route(
    @SerializedName("legs") val legs: List<Leg>?,
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline?,
    @SerializedName("error_message") val error_message: String? = null,
)

data class Leg(
    @SerializedName("steps") val steps: List<Step>?
)

data class Step(
    @SerializedName("polyline") val polyline: Polyline?
)

data class Polyline(
    @SerializedName("points") val points: String?
)

data class OverviewPolyline(
    @SerializedName("points") val points: String?
)