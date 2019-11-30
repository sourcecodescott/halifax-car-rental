package com.example.halifaxcarrental

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.OnSuccessListener


import androidx.core.content.ContextCompat

import android.app.AlertDialog
import android.content.Intent

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest


import android.content.DialogInterface
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import androidx.fragment.app.Fragment
import com.example.halifaxcarrental.car.Car
import com.example.halifaxcarrental.car.CarDetails
import com.example.halifaxcarrental.car.CarListFragment
import com.example.halifaxcarrental.map.PolylineData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

import java.util.ArrayList
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import kotlinx.android.synthetic.main.activity_chat_log.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private val DEFAULT_ZOOM = 12f
    private val TAG = MapFragment::class.java.simpleName

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private lateinit var locationRequest : LocationRequest

    private var db = FirebaseFirestore.getInstance()
    private var carRef = db.collection("Car")

    private var gpsImageView: ImageView? = null
    private var infoImageView: ImageView? = null
    private var carListImageView: ImageView? = null

    private var mGeoApiContext: GeoApiContext? = null

    private var mPolyLinesData = ArrayList<PolylineData>()

    private var mSelectedMarker: Marker? = null
    private var mTripMarkers = ArrayList<Marker>()

    var langg: Double = 0.toDouble()
    var lat: Double = 0.toDouble()

    private var carList = ArrayList<Car>()

    private var showFloatingActions = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)!!.supportActionBar?.title = "Map"

        findReferences()
        getCarList(::getAvaliableCars)
        subscribeforUpdates()

        showHideFloatingActions()
        optionsFloatingActionButton.setOnClickListener { showHideFloatingActions() }
    }

    private fun showHideFloatingActions()
    {
        if (showFloatingActions)
        {
            floatingActionsLayout.visibility = View.VISIBLE
        }

        else
        {
            floatingActionsLayout.visibility = View.GONE
        }

        showFloatingActions = !showFloatingActions
    }

    fun findReferences() {
        gpsImageView = activity!!.findViewById(R.id.refreshh)
        infoImageView = activity!!.findViewById(R.id.place_info)
        carListImageView = activity!!.findViewById(R.id.carlist) as ImageView
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)!!.getMapAsync(this)
    }


    private fun subscribeforUpdates()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!, OnSuccessListener {
                location ->
            if (location == null) {
                createLocationRequest()
                startLocationUpdates()
            }
        }).addOnFailureListener(activity!!) { e -> Log.w("getLastLocation:onFailure", e)}
    }

    fun islogggedin() {
        val intent = activity!!.intent
        if (!intent.hasExtra("success"))
        {
            val newIntent = Intent(activity!!, LoginActivity::class.java)
            startActivity(newIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        islogggedin()

        if (!hasLocationPermissions()) {
            requestPermissions()
        } else {
            getAddress()
        }
    }

    fun requestPermissions(){
        ActivityCompat.requestPermissions(activity!!,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    fun hasLocationPermissions() =
        ActivityCompat.checkSelfPermission(
            activity!!,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            activity!!,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE)
            return
        else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAddress()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        parkingSpot()
        getAvaliableCars()
        addHomeMarker()
        setButtonListeners()

        mMap.isMyLocationEnabled = true

        if (mGeoApiContext == null)
        {
            mGeoApiContext = GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_api_key))
                .build()
        }
    }


    fun addHomeMarker() {
        val mDefaultLocation = LatLng(44.64533, -63.57239)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.home)).title("Home")
            .snippet("Company: Halifax Car Rental\n\nAddress: 123 Park Lane\n                    Halifax NS\n\nTel: 902-342-4567"))
        mMap.moveCamera(CameraUpdateFactory
            .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))
    }

    fun parkingSpot() {
        var locations = setOf(
            LatLng(44.671150, -63.608260),
            LatLng(44.662190, -63.593330),
            LatLng(44.635000, -63.575680),
            LatLng(44.630160, -63.604860),
            LatLng(44.641290, -63.605480),
            LatLng(44.646910, -63.580270),
            LatLng(44.671740, -63.581870),
            LatLng(44.685560, -63.578470),
            LatLng(44.667090, -63.578940),
            LatLng(44.667310, -63.567270)
        )

        for (location in locations.withIndex()) {

            mMap.addMarker(MarkerOptions().position(location.value).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #${location.index + 1}"))
        }
    }

    fun zoomRoute(lstLatLngRoute:List<LatLng>) {
        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty())
            return
        val boundsBuilder = LatLngBounds.Builder()
        for (latLngPoint in lstLatLngRoute)
            boundsBuilder.include(latLngPoint)
        val routePadding = 50
        val latLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
            600, null
        )
    }

    private fun resetSelectedMarker() {
        if (mSelectedMarker != null)
        {
            mSelectedMarker?.isVisible = true
            mSelectedMarker = null
            removeTripMarkers()
        }
    }
    private fun removeTripMarkers() {
        for (marker in mTripMarkers)
        {
            marker.remove()
        }
    }

    private fun resetMap() {
        if (mMap != null)
        {
            mMap.clear()
        }
        if (mPolyLinesData.size > 0)
        {
            mPolyLinesData.clear()
            mPolyLinesData = ArrayList()
        }
        parkingSpot()
        addHomeMarker()
        getAvaliableCars()
        setButtonListeners()
        mMap.setMyLocationEnabled(true)
        //mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(activity!!))
        mMap.setOnPolylineClickListener(this)
    }


    override fun onPolylineClick(polyline:Polyline) {
        var index = 0
        for (polylineData in mPolyLinesData)
        {
            index++
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString())
            if (polyline.getId().equals(polylineData.polyline.getId()))
            {
                polylineData.polyline.setColor(ContextCompat.getColor(activity!!, R.color.blue1))
                polylineData.polyline.setZIndex(1f)
                val endLocation = LatLng(
                    polylineData.leg.endLocation.lat,
                    polylineData.leg.endLocation.lng
                )
                val marker = mMap.addMarker(MarkerOptions()
                    .position(endLocation)
                    .title("Route #" + index)
                    .snippet("Duration: " + polylineData.leg.duration
                    ))
                mTripMarkers.add(marker)
                marker.showInfoWindow()
            }
            else
            {
                polylineData.polyline.setColor(ContextCompat.getColor(activity!!, R.color.darkGrey))
                polylineData.polyline.setZIndex(0f)
            }
        }
    }

    private fun addPolylinesToMap(result:DirectionsResult) {
        Log.d(TAG, "adding polylines to map");
        Handler(Looper.getMainLooper()).post(object:Runnable {
            public override fun run() {
                if (mPolyLinesData.size > 0)
                {
                    for (polylineData in mPolyLinesData)
                    {
                        polylineData.polyline.remove()
                    }
                    mPolyLinesData.clear()
                    mPolyLinesData = ArrayList()
                }
                var duration = 999999999.0
                for (route in result.routes)
                {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString())
                    var decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath())
                    var newDecodedPath:ArrayList<LatLng> = ArrayList()
                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (latLng in decodedPath)
                    {
                        Log.d(TAG, "run: latlng: " + latLng.toString());
                        newDecodedPath.add(LatLng(
                            latLng.lat,
                            latLng.lng
                        ))
                    }
                    /*
                    var polyline = mMap.addPolyline(PolylineOptions().addAll(newDecodedPath))
                    polyline.setColor(ContextCompat.getColor(activity!!, R.color.darkGrey))
                    polyline.setClickable(true)
                    mPolyLinesData.add(
                        PolylineData(
                            polyline,
                            route.legs[0]
                        )
                    )*/

                    val polylineOptions = PolylineOptions()
                        .color(ContextCompat.getColor(activity!!, R.color.darkGrey))
                    var polyline = mMap.addPolyline(polylineOptions.addAll(newDecodedPath))
                    polyline.setClickable(true)
                    mPolyLinesData.add(
                            PolylineData(
                                    polyline,
                                    route.legs[0]
                            )
                    )

                    // highlight the fastest route and adjust camera
                    var tempDuration = route.legs[0].duration.inSeconds
                    if (tempDuration < duration)
                    {
                        duration = tempDuration.toDouble()
                        onPolylineClick(polyline)
                        zoomRoute(polyline.points)
                    }
                    mSelectedMarker?.isVisible = false
                }
            }
        })
    }

    private fun calculateDirections(marker:Marker) {
        Log.d(TAG, "calculateDirections: calculating directions.")
        var destination = com.google.maps.model.LatLng(
            marker.position.latitude,
            marker.position.longitude
        )
        var directions = DirectionsApiRequest(mGeoApiContext)
        directions.alternatives(true)
        directions.origin(
            com.google.maps.model.LatLng(
                marker.position.latitude,
                marker.position.longitude
            )
        )
        Log.d(TAG, "calculateDirections: destination: " + destination.toString())
        directions.destination(destination).setCallback(object:PendingResult.Callback<DirectionsResult> {
            override fun onResult(result:DirectionsResult) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString())
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString())
                addPolylinesToMap(result)
            }
            override fun onFailure(e:Throwable) {
                Log.e(TAG, "onFailure: " + e.message)
            }
        })
    }

    fun getCarList(onCarListSuccess: () -> Unit) {
        carRef.get()
            .addOnSuccessListener(object:OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    carList = ArrayList<Car>()
                    val data = ""
                    val successorfail = false
                    for (documentSnapshot in queryDocumentSnapshots) {
                        var car = documentSnapshot.toObject(Car::class.java)
                        carList.add(car)
                    }

                    onCarListSuccess()
                }
            })
    }

    fun getAvaliableCars() {
        for (car : Car in carList)
        {
            if (car.isavaliable)
            {
                var location = LatLng(java.lang.Double.parseDouble(car.lat.toString()),
                                      java.lang.Double.parseDouble(car.lang.toString()))

                addMapMarker(location, R.drawable.carimage, car)

                mMap.setOnMarkerClickListener(object:GoogleMap.OnMarkerClickListener {
                    override fun onMarkerClick(marker:Marker):Boolean {
                        if (marker.title.equals("Parking")||marker.title.equals("Home"))
                        {
                            var m = marker
                            var builder = AlertDialog.Builder(activity!!)
                            builder.setMessage(marker.getSnippet())
                                .setCancelable(true)
                                .setNegativeButton("Get Directions", object:DialogInterface.OnClickListener {
                                    override fun onClick(dialog:DialogInterface, id:Int) {
                                        calculateDirections(m)
                                        resetSelectedMarker()
                                        mSelectedMarker = m
                                        dialog.dismiss()
                                    }
                                })
                                .setPositiveButton("Close", object:DialogInterface.OnClickListener {
                                    override fun onClick(dialog:DialogInterface, id:Int) {


                                        dialog.dismiss()
                                    }
                                })
                            val alert = builder.create()
                            alert.show()
                        }
                        else
                        {
                            var m = marker
                            var builder = AlertDialog.Builder(activity!!)
                            builder.setMessage(marker.getSnippet())
                                .setCancelable(true)

                                .setNegativeButton("Get Directions", object:DialogInterface.OnClickListener {
                                    override fun onClick(dialog:DialogInterface, id:Int) {
                                        calculateDirections(m)
                                        resetSelectedMarker()
                                        mSelectedMarker = m
                                        dialog.dismiss()
                                    }
                                })
                                .setPositiveButton("More Details...", object:DialogInterface.OnClickListener {
                                    override fun onClick(dialog:DialogInterface, id:Int) {

                                        val intent = Intent(activity!!, CarDetails::class.java)

                                        val sharedData = Globals.instance
                                        sharedData.car_name = m.title
                                        startActivity(intent)

                                        //dialog.dismiss()
                                    }
                                })
                            val alert = builder.create()
                            alert.show()
                        }
                        return true
                    }
                })
            }
            else {
                val sharedData = Globals.instance
                if (sharedData.username == "admin") {

                    var mDefaultLocation1 = LatLng(
                        java.lang.Double.parseDouble(car.lat.toString()),
                        java.lang.Double.parseDouble(car.lang.toString())
                    )

                    addMapMarker(mDefaultLocation1, R.drawable.carimage_rented, car)

                    mMap.setOnMarkerClickListener(object :
                        GoogleMap.OnMarkerClickListener {
                        override fun onMarkerClick(marker: Marker): Boolean {
                            if (marker.title.equals("Parking")||marker.title.equals("Home"))
                            {
                                var m = marker
                                var builder = AlertDialog.Builder(activity!!)
                                builder.setMessage(marker.getSnippet())
                                    .setCancelable(true)

                                    .setNegativeButton("Get Directions", object:DialogInterface.OnClickListener {
                                        override fun onClick(dialog:DialogInterface, id:Int) {
                                            calculateDirections(m)
                                            resetSelectedMarker()
                                            mSelectedMarker = m
                                            dialog.dismiss()
                                        }
                                    })
                                    .setPositiveButton("Close", object:DialogInterface.OnClickListener {
                                        override fun onClick(dialog:DialogInterface, id:Int) {


                                            dialog.dismiss()
                                        }
                                    })
                                val alert = builder.create()
                                alert.show()
                            } else {
                                var m = marker
                                var builder = AlertDialog.Builder(activity!!)
                                builder.setMessage(marker.getSnippet())
                                    .setCancelable(true)

                                    .setNegativeButton(
                                        "Get Directions",
                                        object : DialogInterface.OnClickListener {
                                            override fun onClick(
                                                dialog: DialogInterface,
                                                id: Int
                                            ) {
                                                calculateDirections(m)
                                                resetSelectedMarker()
                                                mSelectedMarker = m
                                                dialog.dismiss()
                                            }
                                        })
                                    .setPositiveButton(
                                        "More Details...",
                                        object : DialogInterface.OnClickListener {
                                            override fun onClick(
                                                dialog: DialogInterface,
                                                id: Int
                                            ) {

                                                val intent = Intent(
                                                    activity!!,
                                                    CarDetails::class.java
                                                )

                                                val sharedData = Globals.instance
                                                sharedData.car_name = m.title
                                                startActivity(intent)

                                                //dialog.dismiss()
                                            }
                                        })
                                val alert = builder.create()
                                alert.show()
                            }
                            return true
                        }
                    })
                }
            }
        }
    }

    private fun addMapMarker(latLng: LatLng, id: Int, car: Car) {
        val markerOptions = MarkerOptions().position(latLng).icon(
            BitmapDescriptorFactory.fromResource(id)
        )
        if (car != null) {
            markerOptions.title(car.name)
            markerOptions.snippet("Make: " + car.make + "\nModel: " + car.model + "\nYear: " + car.year)
        }

        mMap.addMarker(
            markerOptions
        )

        mMap.moveCamera(
            CameraUpdateFactory
                .newLatLngZoom(latLng, DEFAULT_ZOOM)
        )
    }

    private fun setButtonListeners() {

        carListImageView?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                showFloatingActions = false
                showHideFloatingActions()

                val transaction = childFragmentManager.beginTransaction()
                val carList: Fragment = CarListFragment()
                transaction.replace(R.id.constraintLayout, carList)
                transaction.addToBackStack(null)

                transaction.commit()
            }
        })

        gpsImageView?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                showFloatingActions = false
                showHideFloatingActions()
                resetMap()
            }
        })

        infoImageView?.setOnClickListener (object:View.OnClickListener {
            override fun onClick(view:View) {
                showFloatingActions = false
                showHideFloatingActions()
                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle("Map Legend")
                val customLayout = getLayoutInflater().inflate(R.layout.legend_layout, null)
                builder.setView(customLayout)
                builder.setPositiveButton("OK", object:DialogInterface.OnClickListener {
                    override fun onClick(dialog:DialogInterface, which:Int) {
                        // don't need to do anything here, just close the view
                    }
                })
                val dialog = builder.create()
                dialog.show()
            }
        })
    }


    fun getAddress()
    {
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!, OnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(latLng).title("My Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))

            }
        }).addOnFailureListener(activity!!) { e -> Log.w( "getLastLocation:onFailure", e) }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
            Looper.getMainLooper())
    }
}
