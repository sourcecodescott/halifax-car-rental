package com.csci4176.halifaxcarrental

import com.csci4176.halifaxcarrental.car.Car

class Globals {
    var notification_index: String? = null
    var username: String? = null

    var rentPIN: String? = null

    var videoToPlay: String? = null
    var photoToView: String? = null

    var audioToPlay: String? = null

    var talkTo: String? = null

    var car_name: String? = null

    var addedCost: Float? = null

    var rentedCar: Car? = null

    fun getValue(): String? {
        return notification_index
    }

    fun setValue(notification_index: String) {
        this.notification_index = notification_index
    }

    companion object {
        var instance = Globals()
    }
}