package com.csci4176.halifaxcarrental.map

import com.google.android.gms.maps.model.Polyline
import com.google.maps.model.DirectionsLeg

class PolylineData {
    val polyline: Polyline
    val leg: DirectionsLeg

    constructor(polyline: Polyline, leg: DirectionsLeg) {
        this.polyline = polyline
        this.leg = leg
    }
}