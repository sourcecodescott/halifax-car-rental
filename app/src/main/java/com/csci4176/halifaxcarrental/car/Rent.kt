package com.csci4176.halifaxcarrental.car

import com.google.firebase.firestore.Exclude

import java.io.Serializable

class Rent:Serializable {
    var customerID:String? = null
    var carID:String? = null
    var rentPin:String? = null
    var price:Float? = null

    constructor() {}
    constructor(customerID:String, carID:String,rentPin:String,price:Float) {
        this.customerID = customerID
        this.carID = carID
        this.rentPin = rentPin
        this.price = price
    }
}
