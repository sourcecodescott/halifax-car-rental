package com.csci4176.halifaxcarrental

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.csci4176.halifaxcarrental.chat.ChatLogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_image.*

class ViewImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)


        val sharedData = Globals.instance


        Picasso.get().load(sharedData.photoToView).into(imgViewMe)

       /* btnRetu.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(this@ViewImage, ChatLogFragment::class.java)
                startActivity(intent)
            }
        })*/
    }
}
