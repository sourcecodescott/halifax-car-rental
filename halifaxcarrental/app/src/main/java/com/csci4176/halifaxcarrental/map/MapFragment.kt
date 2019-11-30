package com.csci4176.halifaxcarrental

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
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
import android.widget.ImageView

import com.google.android.gms.location.FusedLocationProviderClient

import android.view.*
import androidx.fragment.app.Fragment
import com.csci4176.halifaxcarrental.map.PolylineData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

import java.util.ArrayList
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.csci4176.halifaxcarrental.car.Car
import com.csci4176.halifaxcarrental.car.CarDetails
import com.csci4176.halifaxcarrental.car.CarListFragment
import com.csci4176.halifaxcarrental.chat.ChatList
import com.csci4176.halifaxcarrental.chat.ChatLogFragment
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private lateinit var mMap: GoogleMap
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private lateinit var fusedLocationClient: FusedLocationProviderClient // member var
    private lateinit var locationCallback : LocationCallback

    private lateinit var locationRequest : LocationRequest

    private val DEFAULZOOM = 13.0f


    private var TAG = MapFragment::class.java.simpleName
    // declare it as an Activity member variable
    private var mLocationCallback: LocationCallback? = null


    private val DEFAULT_ZOOM = 12f

    //declare it as a member variable of the MainActivity
    private val mFusedLocationClient: FusedLocationProviderClient? = null

    //declare it as a member variable of the MainActivity
    private val mLocationRequest: LocationRequest? = null
    // declare it as member variable of the MainActivity
    private val REQUEST_ENABLE_SETTINGS_DIALOG = 1
    //declare as an activity global variable
    private val REQUEST_LOCATION_PERMISSION = 3
    //declare it as global variable in the MainActivity class
    private val REQUEST_ENABLE_GPS = 2

    private var db = FirebaseFirestore.getInstance()
    private var carRef = db.collection("Car")


    private var gpsImageView: ImageView? = null
    private var infoImageView: ImageView? = null

    private var carListImageView: ImageView? = null
    private var myHome: ImageView? = null
    private var cChat: ImageView? = null

    private var mGeoApiContext: GeoApiContext? = null

    private var mPolyLinesData = ArrayList<PolylineData>()


    private var mSelectedMarker: Marker? = null
    private var mTripMarkers = ArrayList<Marker>()

    var langg: Double = 0.toDouble()
    var lat: Double = 0.toDouble()


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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)!!.getMapAsync(this)
    }

    fun islogggedin() {
        val intentmymy:Intent
        intentmymy = activity!!.intent
        if (intentmymy.hasExtra("success") == false)
        {
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        islogggedin()

        if (!hasLocationPermissions()) {
            requestPermissions()
        } else {
            getAddress()
        }
    }

    public fun requestPermissions(){
        ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    /* Checking for permissions */
    fun hasLocationPermissions() = hasFineLocationPermission() && hasCoarseLocationPermission()
    fun hasFineLocationPermission() = ActivityCompat.checkSelfPermission(activity!!,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    fun hasCoarseLocationPermission() = ActivityCompat.checkSelfPermission(activity!!,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED



    /* Request onSuccess callback snippet */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return
        if (!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAddress()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        parkingSpot()
        getAvaliableCars()
        home()
        init()

        mMap.setMyLocationEnabled(true)

        if (mGeoApiContext == null)
        {
            mGeoApiContext = GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build()
        }

    }


    fun home() {




        val mDefaultLocation = LatLng(44.64533, -63.57239)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.home)).title("Home")
                .snippet("Company: Halifax Car Rental\n\nAddress: 123 Park Lane\n                    Halifax NS\n\nTel: 902-342-4567"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))

    }

    fun zoomRoute(lstLatLngRoute:List<LatLng>) {
        if (mMap ==
                null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return
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
            mSelectedMarker?.setVisible(true)
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
        home()
        getAvaliableCars()
        init()
        mMap.setMyLocationEnabled(true)
        //mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
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



    fun parkingSpot() {
        var mDefaultLocation = LatLng(44.671150, -63.608260)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #1"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))

        val mDefaultLocation1 = LatLng(44.662190, -63.593330)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation1).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #2"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation1, DEFAULT_ZOOM))
        val mDefaultLocation3 = LatLng(44.635000, -63.575680)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation3).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #3"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation3, DEFAULT_ZOOM))
        val mDefaultLocation4 = LatLng(44.630160, -63.604860)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation4).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #4"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation4, DEFAULT_ZOOM))

        val mDefaultLocation5 = LatLng(44.641290, -63.605480)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation5).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #5"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation5, DEFAULT_ZOOM))
        val mDefaultLocation6 = LatLng(44.646910, -63.580270)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation6).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #6"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation6, DEFAULT_ZOOM))
        val mDefaultLocation7 = LatLng(44.671740, -63.581870)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation7).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #7"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation7, DEFAULT_ZOOM))
        val mDefaultLocation8 = LatLng(44.685560, -63.578470)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation8).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #8"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation8, DEFAULT_ZOOM))
        val mDefaultLocation9 = LatLng(44.667090, -63.578940)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation9).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #9"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation9, DEFAULT_ZOOM))
        val mDefaultLocation10 = LatLng(44.667310, -63.567270)
        mMap.addMarker(MarkerOptions().position(mDefaultLocation10).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)).title("Parking")
                .snippet("Parking #10"))
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mDefaultLocation10, DEFAULT_ZOOM))
    }


    private fun addPolylinesToMap(result:DirectionsResult) {
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
                        // Log.d(TAG, "run: latlng: " + latLng.toString());
                        newDecodedPath.add(LatLng(
                                latLng.lat,
                                latLng.lng
                        ))
                    }
                    var polyline = mMap.addPolyline(PolylineOptions().addAll(newDecodedPath))
                    polyline.setColor(ContextCompat.getColor(activity!!, R.color.darkGrey))
                    polyline.setClickable(true)
                    mPolyLinesData.add(PolylineData(polyline, route.legs[0]))
                    // highlight the fastest route and adjust camera
                    var tempDuration = route.legs[0].duration.inSeconds
                    if (tempDuration < duration)
                    {
                        duration = tempDuration.toDouble()
                        onPolylineClick(polyline)
                        zoomRoute(polyline.getPoints())
                    }
                    mSelectedMarker?.setVisible(false)
                }
            }
        })
    }

    private fun calculateDirections(marker:Marker) {
        Log.d(TAG, "calculateDirections: calculating directions.")
        var destination = com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        )
        var directions = DirectionsApiRequest(mGeoApiContext)
        directions.alternatives(true)
        directions.origin(
                com.google.maps.model.LatLng(
                        lat,
                        langg
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


    fun getAvaliableCars() {
        carRef.get()
                .addOnSuccessListener(object:OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(queryDocumentSnapshots:QuerySnapshot) {
                        val data = ""
                        val successorfail = false
                        for (documentSnapshot in queryDocumentSnapshots)
                        {
                            var car = documentSnapshot.toObject(Car::class.java)
                            var lat = car.lat
                            var lang = car.lang
                            var isavaliable = car.isavaliable
                            if (isavaliable == true)
                            {
                                var mDefaultLocation1 = LatLng(java.lang.Double.parseDouble(lat.toString()), java.lang.Double.parseDouble(lang.toString()))
                                mMap.addMarker(MarkerOptions().position(mDefaultLocation1).icon(BitmapDescriptorFactory.fromResource(R.drawable.carimage))
                                        .title(car.name)
                                        .snippet("Make: " + car.make + "\nModel: " + car.model + "\nYear: " + car.year ))
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation1, DEFAULT_ZOOM))
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
                                            java.lang.Double.parseDouble(lat.toString()),
                                            java.lang.Double.parseDouble(lang.toString())
                                    )
                                    mMap.addMarker(
                                            MarkerOptions().position(mDefaultLocation1).icon(
                                                    BitmapDescriptorFactory.fromResource(R.drawable.carimage_rented)
                                            )
                                                    .title(car.name)
                                                    .snippet("Make: " + car.make + "\nModel: " + car.model + "\nYear: " + car.year )
                                    )
                                    mMap.moveCamera(
                                            CameraUpdateFactory
                                                    .newLatLngZoom(mDefaultLocation1, DEFAULT_ZOOM)
                                    )
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
                })

    }


    private fun init() {
        cChat?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                showFloatingActions = false
                showHideFloatingActions()
                val sharedData = Globals.instance
                if(sharedData.username == "admin")
                {
                    val intent = Intent(activity!!, ChatList::class.java)
                    startActivity(intent)
                }
                else{
                    sharedData.talkTo = "admin"
                    val intent = Intent(activity!!,ChatLogFragment::class.java)
                    startActivity(intent)
                }

            }
        })
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

                //login()
                //-----------------------------------------------------------------------
                // create an alert builder
                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle("Map Legend")
                // set the custom layout
                val customLayout = getLayoutInflater().inflate(R.layout.legend_layout, null)
                builder.setView(customLayout)
                // add a button
                builder.setPositiveButton("OK", object:DialogInterface.OnClickListener {
                    override fun onClick(dialog:DialogInterface, which:Int) {
                        // send data from the AlertDialog to the Activity
                    }
                })
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
                //----------------------------------------------------------------------
            }
        })
    }


    fun getAddress()
    {
        /* Below is getAddress() function snippet */
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!, OnSuccessListener {
            location ->
            if (location != null) {
                location.getLatitude()
                location.getLongitude()

                langg = location.getLongitude()
                lat = location.getLatitude()

                val sydney = LatLng(location.getLatitude(), location.getLongitude())
                mMap.addMarker(MarkerOptions().position(sydney).title("My Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,DEFAULZOOM ))

            }
        }).addOnFailureListener(activity!!) { e -> Log.w( "getLastLocation:onFailure", e) }
    }

    private fun subscribeforUpdates()
    {
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!, OnSuccessListener {
            location ->
            if (location == null) {
                createLocationRequest()
                startLocationUpdates()
            }
        }).addOnFailureListener(activity!!) { e -> Log.w("getLastLocation:onFailure", e)}
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                Looper.getMainLooper())
    }


}
