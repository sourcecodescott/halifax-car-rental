package com.example.halifaxcarrental.car

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.halifaxcarrental.Globals
import com.example.halifaxcarrental.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_car_large.*

class CarDetails : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

    var carRef = db.collection("Car")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cardview_car_large)


    }

    override fun onStart() {
        super.onStart()

        getCar()
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
                                txtAvaliable.text = carr.isavaliable.toString()
                                txtMake.text = carr.make
                                txtModel.text = carr.model
                                txtName.text = carr.name
                                txtPrice.text = "$"+carr.price
                                txtYear.text = carr.year

                                if(!carr.isavaliable)
                                {
                                    btnrent.text = "Car Not Avaliable"
                                }

                                btnrent.setOnClickListener { v ->
                                    Globals.instance.rentedCar = carr
                                }
                            }

                        }
                    }
                }
            })

    }
}
