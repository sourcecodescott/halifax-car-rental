package com.csci4176.halifaxcarrental

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.csci4176.halifaxcarrental.car.Car
import com.csci4176.halifaxcarrental.car.Rent
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.cardview_car_large.*

class LoginActivity : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var userRef = db.collection("User")
    var rentlistRef = db.collection("Rent")

    var carRef = db.collection("Car")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "Login"

        loginButton.setOnClickListener {
            verifyUser()
        }
    }

    fun verifyUser() {
        userRef.get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
            override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                for (documentSnapshot in queryDocumentSnapshots) {
                    val user = documentSnapshot.toObject(User::class.java)
                    if (compareUser(user)) {
                        setUser(user)
                        if(user.username == "admin")
                        {
                            val sharedData = Globals.instance
                            sharedData.rentedCar = null
                            sharedData.car_name = ""
                        }
                        else
                        {
                            setCarName()
                        }

                        goToMainActivity()

                        return
                    }
                }
                Toast.makeText(this@LoginActivity, "Incorrect username or password!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun compareUser(user: User) : Boolean{
        return user != null &&
               user.username == usernameEditText.text.toString() &&
               user.password == passEditText.text.toString()
    }

    fun setUser(user: User) {
        val sharedData = Globals.instance
        sharedData.username = user.username
        sharedData.setValue("success")
    }

    fun setRentedCar()
    {
        val sharedData = Globals.instance

        carRef.get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                        for (documentSnapshot in queryDocumentSnapshots) {
                            val carr = documentSnapshot.toObject(Car::class.java)
                            if (carr != null) {

                                if (carr.name == sharedData.car_name) {

                                    sharedData.rentedCar = carr

                                }

                            }
                        }
                    }
                })
    }

    fun setCarName() {

        val sharedData = Globals.instance
        sharedData.car_name = ""

        rentlistRef.get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                        for (documentSnapshot in queryDocumentSnapshots) {
                            val rentC = documentSnapshot.toObject(Rent::class.java)
                            if (rentC != null) {

                                if (rentC.customerID == sharedData.username) {

                                    sharedData.car_name = rentC.carID
                                    sharedData.rentPIN = rentC.rentPin
                                    setRentedCar()
                                    //Toast.makeText(this@LoginActivity,"Rented Car: "+ sharedData.car_name , Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                    }
                })

    }

    fun goToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("success", "success")
        startActivity(intent)
        finish()
    }
}
