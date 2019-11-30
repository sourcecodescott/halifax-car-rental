package com.csci4176.halifaxcarrental.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_r_car_list.*
import androidx.fragment.app.Fragment
import com.csci4176.halifaxcarrental.R

class CarListFragment : Fragment() {


    var db = FirebaseFirestore.getInstance()
    var refCars = db.collection("Car")
    var adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_r_car_list, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerview_car_log.adapter = adapter
        (activity as AppCompatActivity)!!.supportActionBar?.title = "Car List"

        getCars()
    }

    fun getCars() {


        refCars.get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val car = documentSnapshot.toObject(Car::class.java)
                        if (car != null) {

                            adapter.add(CarList(car))
                        }
                    }
                }
            })
    }
}
