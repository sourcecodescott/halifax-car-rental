package com.csci4176.halifaxcarrental.ar

import com.csci4176.halifaxcarrental.car.Car
import com.google.ar.sceneform.Node
import com.google.maps.model.LatLng
import java.lang.Double

internal class ARLocationMarker(var layoutNode: Node?,
                                var modelNode: Node?,
                                var car: Car) {
    var latLng: LatLng

    init {
        this.latLng = LatLng(Double.parseDouble(car.lat!!), Double.parseDouble(car.lang!!))
    }
}