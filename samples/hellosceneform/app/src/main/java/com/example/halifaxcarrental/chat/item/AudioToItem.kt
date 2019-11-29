package com.example.halifaxcarrental.chat.item

import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.halifaxcarrental.Globals
import com.example.halifaxcarrental.chat.PlayAudio
import com.example.halifaxcarrental.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
//import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.image_to.view.*

class AudioToItem(val audiocontent: String, val user: String, val fragmentManager: FragmentManager): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val tn = "https://firebasestorage.googleapis.com/v0/b/courseregistration-8ddaa.appspot.com/o/files%2Ftransparent-speaker-icon-29.jpg?alt=media&token=07cbe6f5-185d-4f4b-b314-3a26c809d61e"



        Picasso.get().load(tn).into(viewHolder.itemView.image_content_to)

        // load our user image into the star
        if(user != "") {
            val uri = user
            val targetImageView = viewHolder.itemView.imageview_chat_to_row
            Picasso.get().load(uri).into(targetImageView)
        }

        viewHolder.itemView.image_content_to?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                val fragment = PlayAudio()

                //val intent = Intent(view.context, PlayAudio::class.java)

                if(audiocontent != "") {
                    val sharedData = Globals.instance
                    sharedData.audioToPlay= audiocontent
                    val transaction = fragmentManager.beginTransaction()

                    transaction.replace(R.id.constraintLayout, fragment)
                    transaction.addToBackStack(null)

                    transaction.commit()
                    //view.context.startActivity(intent)
                    // Toast.makeText(view.context, sharedData.videoToPlay , Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.image_to
    }
}