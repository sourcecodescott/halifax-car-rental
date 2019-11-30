/*
 * Marked parts of this file have been converted to kotlin from Google's hellosceneform java application
 *
 * Copyright 2018 Google LLC. All Rights Reserved.
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
package com.csci4176.halifaxcarrental

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.csci4176.halifaxcarrental.ar.ARLocationMarker
import com.csci4176.halifaxcarrental.car.Car
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import uk.co.appoly.arcorelocation.LocationMarker
import android.view.WindowManager

import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper
import java.util.ArrayList
import java.util.concurrent.CompletableFuture

class ARActivity : AppCompatActivity() {
    private var loadingMessageSnackbar: Snackbar? = null

    private var rentedCarLayoutRenderable: ViewRenderable? = null
    private var rentedCarLayout: CompletableFuture<ViewRenderable>? = null

    private var availableCarLayoutRenderable: ViewRenderable? = null
    private var availableCarLayout: CompletableFuture<ViewRenderable>? = null

    private var locationScene: LocationScene? = null

    private var arVehicleMarkers: ArrayList<ARLocationMarker>? = null

    private var arFragment: ArFragment? = null
    private var markerRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_ux)

        getReferences()

        buildMarkerModel()
        buildLayouts()

        /* Converted from hellosceneform */
        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            if (markerRenderable == null) {
                return@setOnTapArPlaneListener
            }
            // Create the Anchor.
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment!!.arSceneView.scene)
            // Create the transformable andy and add it to the anchor.
            val model = TransformableNode(arFragment!!.transformationSystem)
            model.setParent(anchorNode)
            model.renderable = markerRenderable
            model.select()
        }

        if (true) {
            val db = FirebaseFirestore.getInstance()
            val cars = db.collection("Car")

            createVehicleMarkersFromCollectionReference(cars)

            addMarkers()
        }
    }

    private fun createVehicleMarkersFromCollectionReference(cars: CollectionReference) {
        arVehicleMarkers = ArrayList()
        cars.get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val car = documentSnapshot.toObject(Car::class.java)
                        if (car != null) {
                            val arVehicleMarker = createVehicleMarker(car)

                            arVehicleMarkers!!.add(arVehicleMarker)
                        }
                    }
                }
    }

    private fun createVehicleMarker(car: Car): ARLocationMarker {
        return ARLocationMarker(getView(car), getMarker(), car)
    }

    private fun getMarker() : Node {
        val node = Node()
        node.renderable = markerRenderable
        node.setOnTapListener {hitTestResult, motionEvent ->
            Toast.makeText(
                    this, "Marker touched.", Toast.LENGTH_LONG)
                    .show()
        }
        return node
    }

    private fun getView(car: Car) : Node {
        val node = Node()
        if (car.isavaliable)
            node.renderable = availableCarLayoutRenderable
        else node.renderable = rentedCarLayoutRenderable
        node.setOnTapListener {hitTestResult, motionEvent ->
            Toast.makeText(
                    this, "Layout touched.", Toast.LENGTH_LONG)
                    .show()
        }
        return node
    }
    fun getReferences() {
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?
        locationScene = LocationScene(this.applicationContext, this@ARActivity, arFragment!!.arSceneView)
    }

    fun buildMarkerModel() {
        /* Converted from hellosceneform */
        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
                .setSource(this, R.raw.model)
                .build()
                .thenAccept { renderable: ModelRenderable? -> markerRenderable = renderable }
                .exceptionally { //: Throwable? ->
                    val toast = Toast.makeText(this, "Unable to load mwrker renderable", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }
    }

    fun buildLayouts() {
        availableCarLayout = ViewRenderable.builder()
                .setView(this@ARActivity, R.layout.ar_available_car_layout)
                .build()
        rentedCarLayout = ViewRenderable.builder()
                .setView(this@ARActivity, R.layout.ar_rented_car_layout)
                .build()
    }

    /* Converted from hellosceneform */
    companion object {
        private val TAG: String = ARActivity.javaClass.simpleName
        private const val MIN_OPENGL_VERSION = 3.0
        /**
         * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
         * on this device.
         *
         *
         * Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
         *
         *
         * Finishes the activity if Sceneform can not run
         */
        fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
            if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
                Log.e(TAG, "Sceneform requires Android N or later")
                Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show()
                activity.finish()
                return false
            }
            val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                    .deviceConfigurationInfo
                    .glEsVersion
            if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
                Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
                Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                        .show()
                activity.finish()
                return false
            }
            return true
        }
    }

    private fun addMarkers() {
        for (arVehicleMarker in arVehicleMarkers!!) {
            try {
                addMarker(arVehicleMarker)

            } catch (e: Exception) {
                println("Error finding car renderable: " + e.message)
            }

        }
    }

    private fun addMarker(arVehicleMarker: ARLocationMarker)
    {
        System.out.println(("Adding marker for car " + arVehicleMarker.car.make + ", " + arVehicleMarker.car.model))

        var node = arVehicleMarker.modelNode

        var layoutLocationMarker = LocationMarker(
                arVehicleMarker.latLng.lng,
                arVehicleMarker.latLng.lat,
                node
        )

        // Adding the marker
        locationScene!!.mLocationMarkers.add(layoutLocationMarker)


        node = arVehicleMarker.layoutNode
        layoutLocationMarker = LocationMarker(
                arVehicleMarker.latLng.lng,
                arVehicleMarker.latLng.lat,
                node
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

        System.out.println(("Marker added for car " + arVehicleMarker.car.make + ", " + arVehicleMarker.car.model))
    }

    private fun setArSceneUpdateListener(renderablesLoaded: Boolean) {
        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        arFragment!!.arSceneView!!.scene.addOnUpdateListener { frameTime ->

                if (locationScene == null) {
                    // If our locationScene object hasn't been setup yet, this is a good time to do it
                    // We know that here, the AR components have been initiated.
                    locationScene = LocationScene(this, this,  arFragment!!.arSceneView!!)

                    if (renderablesLoaded)
                        addMarkers()
                }

                val frame =  arFragment!!.arSceneView!!.arFrame
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

    @Throws(UnavailableException::class)
    fun createArSession(): Session? {
        var session: Session? = null
        // if we have the camera permission, create the session
        if (ARLocationPermissionHelper.hasPermission(this@ARActivity)) {
            when (ArCoreApk.getInstance().requestInstall(this@ARActivity,false)) {
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

        if (arFragment!!.arSceneView!!.session == null) {
            try {
                val session = createArSession()
                if (session != null) {
                    arFragment!!.arSceneView!!.setupSession(session)
                }
            } catch (e: UnavailableException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }

        }

        try {
            arFragment!!.arSceneView!!.resume()
        } catch (ex: CameraNotAvailableException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (arFragment!!.arSceneView!!.session != null) {
            showLoadingMessage()
        }
    }

    public override fun onPause() {
        super.onPause()

        if (locationScene != null) {
            locationScene!!.pause()
        }

        arFragment!!.arSceneView!!.pause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        arFragment!!.arSceneView!!.destroy()
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