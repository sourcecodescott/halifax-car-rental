package com.csci4176.halifaxcarrental.car

import java.io.Serializable

class Car:Serializable {
    var name:String? = null
    var make:String? = null
    var model:String? = null
    var lang:String? = null
    var lat:String? = null
    var year:String? = null
    var isavaliable:Boolean = false
    var car_image:String? = null
    var price:String? = null
    constructor() {}
    constructor(name:String, make:String, model:String, lang:String, lat:String, year:String, isavaliable:Boolean,car_image:String,price:String) {
        this.name = name
        this.make = make
        this.model = model
        this.lang = lang
        this.lat = lat
        this.year = year
        this.isavaliable = isavaliable
        this.car_image = car_image
        this.price = price
    }
}