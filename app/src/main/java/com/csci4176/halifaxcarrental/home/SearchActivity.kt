package com.csci4176.halifaxcarrental.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.csci4176.halifaxcarrental.R
import com.csci4176.halifaxcarrental.car.CarListFragment

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val transaction = supportFragmentManager.beginTransaction()
        val carList: Fragment = CarListFragment()
        transaction.replace(R.id.scrollView, carList)

        transaction.commit()
    }
}
