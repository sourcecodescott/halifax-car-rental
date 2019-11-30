package com.csci4176.halifaxcarrental.car

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_car_large.*
import java.util.*

class CarDetails : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

    var carRef = db.collection("Car")
    var rentlistRef = db.collection("Rent")

    var  ischeck = true
    var  yourCar = false
    var carPricee = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cardview_car_large)

        getRentList()

        btnrent.setOnClickListener {
            val sharedData = Globals.instance

            if(yourCar == false)
            {
                saveRent(sharedData.username.toString(),sharedData.car_name.toString())

            }
            else
            {
                returnCar()
            }

        }
    }


    fun saveRent(customerID:String, rentID:String) {

        val sharedData = Globals.instance
        var random = Random()
        var generatedPin = String.format("%04d", random.nextInt(10000))

        var rentRef = db.collection("Rent").document(sharedData.username.toString())

        var r = Rent(customerID,rentID,generatedPin,carPricee)
        rentRef.set(r).addOnSuccessListener {

            Toast.makeText(this@CarDetails, "Car Successfully Rented!", Toast.LENGTH_SHORT).show()
            btnrent.text = "Return Car"
            btnrent.setBackgroundColor(Color.RED)
            yourCar = true
        }

    }

    fun returnCar()
    {
        val sharedData = Globals.instance
        db.collection("Rent").document(sharedData.username.toString())
                .delete()



        Toast.makeText(this@CarDetails, "Car Successfully Returned!!", Toast.LENGTH_SHORT).show()
        btnrent.text = "Rent Car"
        btnrent.setBackgroundColor(Color.GREEN)
        yourCar = false
    }

    override fun onStart() {
        super.onStart()

        getCar()
    }


    fun getRentList() {

        val sharedData = Globals.instance

        rentlistRef.get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                        for (documentSnapshot in queryDocumentSnapshots) {
                            val rentC = documentSnapshot.toObject(Rent::class.java)
                            if (rentC != null) {

                                if (rentC.customerID == sharedData.username && rentC.carID == sharedData.car_name) {

                                    btnrent.text = "Return Car"
                                    btnrent.setBackgroundColor(Color.RED)
                                    yourCar = true

                                }

                                else if (rentC.customerID == sharedData.username) {

                                    btnrent.text = "You Already have a car Rented"
                                    btnrent.isEnabled = false
                                    btnrent.setBackgroundColor(Color.GRAY)

                                }

                                else if(rentC.carID == sharedData.car_name)
                                {
                                    btnrent.text = "Car Not Avaliable"
                                    btnrent.isEnabled = false
                                    btnrent.setBackgroundColor(Color.GRAY)

                                }

                            }
                        }
                    }
                })

    }



    fun getCar() {

        val sharedData = Globals.instance

        carRef.get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                        for (documentSnapshot in queryDocumentSnapshots) {
                            val carr = documentSnapshot.toObject(Car::class.java)
                            if (carr != null) {

                                if (carr.name == sharedData.car_name) {

                                    Picasso.get().load(carr.car_image).into(imgCarr)
                                    txtMake.text = carr.make
                                    txtModel.text = carr.model
                                    txtName.text = carr.name
                                    txtPrice.text = "$"+carr.price
                                    txtYear.text = carr.year
                                    carPricee = carr.price!!.toFloat()

                                }

                            }
                        }
                    }
                })

    }
}
