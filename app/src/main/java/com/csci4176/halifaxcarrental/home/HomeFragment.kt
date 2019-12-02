package com.csci4176.halifaxcarrental.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.LoginActivity
import com.csci4176.halifaxcarrental.R
import com.csci4176.halifaxcarrental.car.CarListFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.cardview_car_large.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.optionsFloatingActionButton


class HomeFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var floatingActionsLayout: View

    private lateinit var searchCardView: CardView
    private lateinit var arCardView: CardView

    private lateinit var rentedCarText: TextView
    private lateinit var carCardView: CardView

    private lateinit var returnButton: Button
    var db = FirebaseFirestore.getInstance()

    private var showFloatingActions = false
    private lateinit var optionsFloatingActionButton: FloatingActionButton
    private lateinit var logoutButton: FloatingActionButton
    val sharedData = Globals.instance
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        getReferences(root)
        setListeners()






        return root
    }


    override fun onStart() {
        super.onStart()

        if (sharedData.rentedCar != null) {
            returnButton.visibility = View.VISIBLE
            rentedCarText.visibility = View.VISIBLE
            carCardView.visibility = View.VISIBLE
             val distanceTextView = carCardView.findViewById<TextView>(R.id.txtDistance)
            val nameTextView = carCardView.findViewById<TextView>(R.id.txtName)
            val makeTextView = carCardView.findViewById<TextView>(R.id.txtMake)
            val modelTextView = carCardView.findViewById<TextView>(R.id.txtModel)
            val yearTextView = carCardView.findViewById<TextView>(R.id.txtYear)
            val availableTextView = carCardView.findViewById<TextView>(R.id.txtAvailable)
            val priceTextView = carCardView.findViewById<TextView>(R.id.txtFee)

            nameTextView.text = sharedData.rentedCar!!.name
            makeTextView.text = sharedData.rentedCar!!.make
            modelTextView.text = sharedData.rentedCar!!.model
            yearTextView.text = sharedData.rentedCar!!.year
            availableTextView.text = "Rented"
            distanceTextView.text = "PIN: "+sharedData.rentPIN
            priceTextView.text = "$" + sharedData.rentedCar!!.price

            Picasso.get()
                    .load(sharedData.rentedCar!!.car_image)
                    .into(carCardView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.img_Car_Img))
        }

        else {
            returnButton.visibility = View.GONE
            rentedCarText.visibility = View.GONE
            carCardView.visibility = View.GONE
        }
        showHideFloatingActions()
    }

    private fun logout() {
        val sharedData = Globals.instance
        sharedData.username = null

        val intent = Intent(activity!!, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showHideFloatingActions()
    {
        if (showFloatingActions)
        {
            floatingActionsLayout.visibility = View.VISIBLE
        }

        else
        {
            floatingActionsLayout.visibility = View.GONE
        }

        showFloatingActions = !showFloatingActions
    }

    fun returnCar()
    {
        val sharedData = Globals.instance
        db.collection("Rent").document(sharedData.username.toString())
                .delete()




        db.collection("Car").document(sharedData.car_name.toString())
                .update(
                        "isavaliable", true
                )

        sharedData.car_name = ""
        sharedData.rentedCar = null
        sharedData.rentPIN = ""

        Toast.makeText(this.context, "Car Successfully Returned!!", Toast.LENGTH_SHORT).show()

    }


    fun getReferences(root: View)
    {
        floatingActionsLayout = root.findViewById(R.id.floatingActionsLayout)
        optionsFloatingActionButton = root.findViewById(R.id.optionsFloatingActionButton)
        logoutButton = root.findViewById(R.id.logoutButton)

        optionsFloatingActionButton.setOnClickListener { showHideFloatingActions() }
        logoutButton.setOnClickListener { logout() }

        returnButton = root.findViewById(R.id.returnButton)
        returnButton.setOnClickListener { v ->
            returnCar()









            returnButton.visibility = View.GONE
            rentedCarText.visibility = View.GONE
            carCardView.visibility = View.GONE
        }
        rentedCarText = root.findViewById(R.id.rentedCarTextView)
        carCardView = root.findViewById(R.id.carCardView)
        searchCardView = root.findViewById(R.id.searchCardView)
        arCardView = root.findViewById(R.id.arCardView)
    }

    fun setListeners()
    {
        searchCardView.setOnClickListener {

            goToSearchActivity()
        }
        arCardView.setOnClickListener { goToARActivity() }
    }

    fun goToSearchActivity()
    {
        val intent = Intent(this.context, SearchActivity::class.java)
        startActivity(intent)
    }

    fun goToARActivity()
    {
        if (!verifyCameraPermissions())
            requestCameraPermissions()

        if (verifyCameraPermissions()) {
            val intent = Intent(this.context, com.csci4176.halifaxcarrental.ar.ARActivity::class.java)
            startActivity(intent)
        }

        else
        {
            Toast.makeText(context, "Can't use AR without camera", Toast.LENGTH_LONG)
        }
    }

    private fun verifyCameraPermissions() : Boolean {
        return ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.CAMERA
            ),
            REQUEST_IMAGE_CAPTURE
        )
    }
}