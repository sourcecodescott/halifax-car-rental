package com.example.halifaxcarrental.ar

import com.example.halifaxcarrental.car.Car
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.maps.model.LatLng
import java.lang.Double
import java.util.concurrent.CompletableFuture

internal class ARLocationMarker(var layout: CompletableFuture<ViewRenderable>, var car: Car) {
    var latLng: LatLng

    init {
        this.latLng = LatLng(Double.parseDouble(car.lat!!), Double.parseDouble(car.lang!!))
    }
}