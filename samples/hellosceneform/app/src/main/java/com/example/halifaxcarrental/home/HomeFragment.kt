package com.example.halifaxcarrental.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.halifaxcarrental.ARActivity
import com.example.halifaxcarrental.Globals
import com.example.halifaxcarrental.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_car_large.*
import kotlinx.android.synthetic.main.cardview_car_small.*
import org.w3c.dom.Text


class HomeFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var searchCardView: CardView
    private lateinit var arCardView: CardView

    private lateinit var rentedCarText: TextView
    private lateinit var carCardView: CardView

    private lateinit var returnButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        getReferences(root)
        setListeners()

        if (Globals.instance.rentedCar != null) {
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

            nameTextView.text = Globals.instance.rentedCar!!.name
            makeTextView.text = Globals.instance.rentedCar!!.make
            modelTextView.text = Globals.instance.rentedCar!!.model
            yearTextView.text = Globals.instance.rentedCar!!.year
            availableTextView.text = "Rented"
            distanceTextView.text = "Nearby"
            priceTextView.text = "$" + Globals.instance.rentedCar!!.price

            Picasso.get()
                    .load(Globals.instance.rentedCar!!.car_image)
                    .into(carCardView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.img_Car_Img))
        }

        else {
            returnButton.visibility = View.GONE
            rentedCarText.visibility = View.GONE
            carCardView.visibility = View.GONE
        }

        return root
    }

    fun getReferences(root: View)
    {
        returnButton = root.findViewById(R.id.returnButton)
        returnButton.setOnClickListener { v->
            Globals.instance.rentedCar = null

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
        searchCardView.setOnClickListener { goToSearchActivity() }
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
            val intent = Intent(this.context, com.example.halifaxcarrental.ar.ARActivity::class.java)
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