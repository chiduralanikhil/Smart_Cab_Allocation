package com.example.uberclone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.uberclone.databinding.FragmentMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.await

class MapsFragment : Fragment() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_CODE = 1

    private lateinit var googleMap: GoogleMap
    private var locationMarker: MarkerOptions? = null
    private lateinit var latLng: LatLng

    private lateinit var binding: FragmentMapsBinding
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                val latitude = location.latitude
                val longitude = location.longitude

                Log.d(
                    "LocationUpdates",
                    " " + LatLng(latitude, longitude).latitude + " " + LatLng(
                        latitude,
                        longitude
                    ).longitude
                )
                latLng = LatLng(location.latitude, location.longitude)
                updateLocationMarker(LatLng(latitude, longitude))
            }
        }
    }


    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        startLocationUpdates()
//        checkLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { Places.initialize(it, getString(R.string.api_key)) }
        autoCompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autoCompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autoCompleteFragment.setCountries("IN")

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Log.d("popopo",p0.statusMessage.toString())
                Toast.makeText(context, "Error in searching", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(p0: Place) {
                val latlngDest = p0.latLng!!
//                googleMap.clear()

                val car1 = LatLng((latLng.latitude)+0.15001, (latLng.longitude)+0.0001)
                val car2 = LatLng((latLng.latitude)-0.0001, (latLng.longitude)+0.10001)
                val car3 = LatLng((latLng.latitude)+0.01204, (latLng.longitude)-0.22056)
                val car4 = LatLng((latLng.latitude)-0.203001, (latLng.longitude)-0.0001)
                val car5 = LatLng((latLng.latitude)-0.023001, (latLng.longitude)+0.03001)


                updateLocationMarker(latLng)
                updateLocationMarker(latlngDest)
                updateLocationMarker(car1)
                updateLocationMarker(car2)
                updateLocationMarker(car3)
                updateLocationMarker(car4)
                updateLocationMarker(car5)

                val locationMarker1 = MarkerOptions().position(car1).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.car))
                val locationMarker2  = MarkerOptions().position(car2).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.car))
                val locationMarker3  = MarkerOptions().position(car3).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.car))
                val locationMarker4  = MarkerOptions().position(car4).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.car))
                val locationMarker5  = MarkerOptions().position(car5).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.car))

                locationMarker = MarkerOptions().position(latlngDest).title("Destination").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.blue_marker))


                googleMap.addMarker(locationMarker1)
                googleMap.addMarker(locationMarker2)
                googleMap.addMarker(locationMarker3)
                googleMap.addMarker(locationMarker4)
                googleMap.addMarker(locationMarker5)
                googleMap.addMarker(locationMarker!!)
                getDirections(latLng, latlngDest)
                zoomOnMap(latlngDest)
            }
        })

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
//        checkLocationPermission()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest().apply {
            interval = 30000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateLocationMarker(latLng: LatLng) {
        if (locationMarker == null) {
            locationMarker = MarkerOptions().position(latLng).title("My Location")
            googleMap.addMarker(locationMarker!!)
        } else {
            locationMarker?.position(latLng)
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }

    private fun zoomOnMap(latlng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latlng, 12f)
        googleMap.animateCamera(newLatLngZoom)
    }
    private fun getDirections(source: LatLng, destination: LatLng) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = RetroInstance.api.getDirections(
                    "mode",
                    "less_driving",
                    "${source.latitude},${source.longitude}",
                    "${destination.latitude},${destination.longitude}",
                    getString(R.string.api_key)
                ).await()

                withContext(Dispatchers.Main) {
                    Log.e("Directions APi", response.toString())
                    handleDirectionsResponse(response)
                }

            } catch (e: Exception) {
                // Handle error
                Log.e("DirectionsAPI", "Error: ${e}")
            }
        }
    }

    private fun handleDirectionsResponse(directionResponse: DirectionResponse?) {
        if (directionResponse != null) {
                val routelist: List<Route>? = directionResponse.routes
                val polyLineList = ArrayList<LatLng>()

                if (routelist != null) {
                    for (i in routelist) {
                        val leg = i.legs?.get(0)
                        val steps = leg?.steps
                        if (steps != null) {
                            for (step in steps) {
                                val points = step.polyline?.points
                                polyLineList.addAll(decodePoly(points))
                            }
                        }
                    }

                    val polylineOptions = PolylineOptions()
                        .color(ContextCompat.getColor(requireContext(), R.color.black))
                        .width(13F)
                        .startCap(RoundCap())
                        .jointType(JointType.ROUND)
                        .addAll(polyLineList)

                    googleMap.addPolyline(polylineOptions)
                }
        } else {
            // Handle error
            Log.e("DirectionsAPI", "Error: essage()}")
        }
    }
    private fun decodePoly(encoded: String?): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded?.length ?: 0
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded!![index++].toInt() - 63
                result = result or (b and 0x1F shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1F shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(p)
        }

        return poly
    }
}
