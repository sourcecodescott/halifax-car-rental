/*
 * Copyright 2018 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.halifaxcarrental.ar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

import com.example.halifaxcarrental.Globals
import com.example.halifaxcarrental.R
import com.example.halifaxcarrental.car.Car
import com.example.halifaxcarrental.car.CarDetails
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.maps.model.LatLng

import java.util.ArrayList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.rendering.LocationNode
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore and Sceneform APIs.
 */
class ARActivity : AppCompatActivity() {
    private val AR_PERMISSIONS = 1

    private val installRequested: Boolean = false
    private var hasFinishedLoading = false

    private var loadingMessageSnackbar: Snackbar? = null

    private var arSceneView: ArSceneView? = null

    private var rentedCarLayoutRenderable: ViewRenderable? = null
    private var rentedCarLayout: CompletableFuture<ViewRenderable>? = null

    private var availableCarLayoutRenderable: ViewRenderable? = null
    private var availableCarLayout: CompletableFuture<ViewRenderable>? = null

    private var locationScene: LocationScene? = null

    private var ARVehicleMarkers: ArrayList<ARLocationMarker>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        getReferences()
        getPermissions()

        buildRenderables()

        val renderablesLoaded = verifyRenderables()

        setArSceneUpdateListener(renderablesLoaded)

        if (renderablesLoaded) {
            val db = FirebaseFirestore.getInstance()
            val cars = db.collection("Car")

            createVehicleMarkersFromCollectionReference(cars)

            addMarkers()
        }
    }

    private fun createVehicleMarkersFromCollectionReference(cars: CollectionReference) {
        ARVehicleMarkers = ArrayList()

        cars.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                for (documentSnapshot in queryDocumentSnapshots) {
                    val car = documentSnapshot.toObject(Car::class.java)
                    if (car != null) {
                        val ARVehicleMarker = createVehicleMarker(car)

                        ARVehicleMarkers!!.add(ARVehicleMarker)
                    }
                }
            }
    }

    private fun createVehicleMarker(car: Car): ARLocationMarker {
        val layout: CompletableFuture<ViewRenderable>

        if (car.isavaliable) {
            layout = ViewRenderable.builder()
                .setView(this@ARActivity, R.layout.ar_available_car_layout)
                .build()
        } else {
            layout = ViewRenderable.builder()
                .setView(this@ARActivity, R.layout.ar_rented_car_layout)
                .build()
        }

        return ARLocationMarker(layout, car)
    }

    private fun getReferences() {
        arSceneView =
            (supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment).arSceneView
    }

    private fun hasPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getPermissions() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                AR_PERMISSIONS
            )
        }
    }

    private fun buildRenderables() {
        rentedCarLayout = ViewRenderable.builder()
            .setView(this, R.layout.ar_rented_car_layout)
            .build()

        availableCarLayout = ViewRenderable.builder()
            .setView(this, R.layout.ar_available_car_layout)
            .build()
    }

    private fun verifyRenderables(): Boolean {
        CompletableFuture.allOf(rentedCarLayout, availableCarLayout).handle { notUsed, throwable ->
            if (throwable == null) {
                try {
                    rentedCarLayoutRenderable = rentedCarLayout!!.get()
                    availableCarLayoutRenderable = availableCarLayout!!.get()
                    hasFinishedLoading = true

                    true
                } catch (ex: InterruptedException) {
                    false
                } catch (ex: ExecutionException) {
                    false
                }
            }
        }
        return true
    }

    private fun setArSceneUpdateListener(renderablesLoaded: Boolean) {
        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        arSceneView!!.scene.addOnUpdateListener { frameTime ->
            if (hasFinishedLoading) {
                if (locationScene == null) {
                    // If our locationScene object hasn't been setup yet, this is a good time to do it
                    // We know that here, the AR components have been initiated.
                    locationScene = LocationScene(this, this, arSceneView!!)

                    if (renderablesLoaded)
                        addMarkers()
                }

                val frame = arSceneView!!.arFrame
                if (frame != null) {
                    if (frame!!.camera.trackingState == TrackingState.TRACKING) {

                        if (locationScene != null) {
                            locationScene!!.processFrame(frame)
                        }

                        if (loadingMessageSnackbar != null) {
                            for (plane in frame.getUpdatedTrackables(Plane::class.java)) {
                                if (plane.trackingState == TrackingState.TRACKING) {
                                    hideLoadingMessage()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addMarkers() {
        for (ARVehicleMarker in ARVehicleMarkers!!) {
            try {
                val renderable = ARVehicleMarker.layout.get()

                val layoutLocationMarker = LocationMarker(
                    ARVehicleMarker.latLng.lng,
                    ARVehicleMarker.latLng.lat,

                    getCarView(availableCarLayoutRenderable, ARVehicleMarker)
                )

                // An example "onRender" event, called every frame
                // Updates the layout with the markers distance
                layoutLocationMarker.renderEvent =
                    LocationNodeRender { node ->
                        val eView = rentedCarLayoutRenderable!!.view
                        val carCardView = eView.findViewById<View>(R.id.carCardView)

                        val distanceTextView = carCardView.findViewById<TextView>(R.id.txtDistance)
                        val nameTextView = carCardView.findViewById<TextView>(R.id.txtName)
                        val makeTextView = carCardView.findViewById<TextView>(R.id.txtMake)
                        val modelTextView = carCardView.findViewById<TextView>(R.id.txtModel)
                        val yearTextView = carCardView.findViewById<TextView>(R.id.txtYear)

                        val text = node.distance.toString() + "M away"
                        distanceTextView.text = text
                    }

                // Adding the marker
                locationScene!!.mLocationMarkers.add(layoutLocationMarker)
            } catch (e: Exception) {
                println("Error finding car renderable: " + e.message)
            }

        }
    }

    private fun updateMarkerPositions() {
        // TODO
    }

    private fun getCarView(
        viewRenderable: ViewRenderable?,
        ARVehicleMarker: ARLocationMarker
    ): Node {
        val base = Node()
        base.renderable = viewRenderable
        val c = this
        // Add  listeners etc here
        val eView = viewRenderable!!.view
        eView.setOnTouchListener { v, event ->
            val intent = Intent(this@ARActivity, CarDetails::class.java)

            intent.putExtra("car", ARVehicleMarker.car)
            startActivity(intent)

            val sharedData = Globals.instance
            sharedData.car_name = ARVehicleMarker.car.name
            startActivity(intent)

            v.performClick()

            Toast.makeText(
                c, "Location marker touched.", Toast.LENGTH_LONG
            )
                .show()
            false
        }

        return base
    }

    @Throws(UnavailableException::class)
    fun createArSession(): Session? {
        var session: Session? = null
        // if we have the camera permission, create the session
        if (ARLocationPermissionHelper.hasPermission(this@ARActivity)) {
            when (ArCoreApk.getInstance().requestInstall(this@ARActivity, !installRequested)) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> return null
                ArCoreApk.InstallStatus.INSTALLED -> {
                }
            }
            session = Session(this@ARActivity)
            val config = Config(session)
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            session.configure(config)
        }
        return session
    }



    /**
     * Make sure we call locationScene.resume();
     */
    override fun onResume() {
        super.onResume()

        if (locationScene != null) {
            locationScene!!.resume()
        }

        if (arSceneView!!.session == null) {
            try {
                val session = createArSession()
                if (session != null) {
                    arSceneView!!.setupSession(session)
                }
            } catch (e: UnavailableException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }

        }

        try {
            arSceneView!!.resume()
        } catch (ex: CameraNotAvailableException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (arSceneView!!.session != null) {
            showLoadingMessage()
        }
    }

    public override fun onPause() {
        super.onPause()

        if (locationScene != null) {
            locationScene!!.pause()
        }

        arSceneView!!.pause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        arSceneView!!.destroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        if (!hasPermissions()) {
            run {
                Toast.makeText(
                    this,
                    "Camera and location permissions are needed to use this feature",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window
                .decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }


    private fun showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar!!.isShownOrQueued) {
            return
        }

        loadingMessageSnackbar = Snackbar.make(
            this@ARActivity.findViewById(android.R.id.content),
            "Finding surfaces...",
            Snackbar.LENGTH_INDEFINITE
        )
        loadingMessageSnackbar!!.view.setBackgroundColor(-0x40cdcdce)
        loadingMessageSnackbar!!.show()
    }

    private fun hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return
        }

        loadingMessageSnackbar!!.dismiss()
        loadingMessageSnackbar = null
    }
}
