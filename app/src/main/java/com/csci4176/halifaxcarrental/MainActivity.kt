package com.csci4176.halifaxcarrental

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

import com.csci4176.halifaxcarrental.chat.ChatLogFragment
import com.google.android.gms.common.util.CollectionUtils.setOf
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        var set: Set<Int> = setOf(
            R.id.navigation_home, R.id.navigation_map, R.id.navigation_notifications
        )

        val appBarConfiguration : AppBarConfiguration = AppBarConfiguration(set)

        setupActionBarWithNavController(this as androidx.appcompat.app.AppCompatActivity, navController!!, appBarConfiguration)
        navView.setupWithNavController(navController!!)
    }

    fun goToChat() {

        val transaction = supportFragmentManager.beginTransaction()
        val fragment: Fragment = ChatLogFragment()
        transaction.replace(R.id.constraintLayout, fragment)
        transaction.addToBackStack(null)

        transaction.commit()
    }
}
