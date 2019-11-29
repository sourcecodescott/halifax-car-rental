package com.example.halifaxcarrental.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.halifaxcarrental.ARActivity
import com.example.halifaxcarrental.R


class HomeFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var searchCardView: CardView
    private lateinit var arCardView: CardView

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

    fun getReferences(root: View)
    {
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
            val intent = Intent(this.context, ARActivity::class.java)
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