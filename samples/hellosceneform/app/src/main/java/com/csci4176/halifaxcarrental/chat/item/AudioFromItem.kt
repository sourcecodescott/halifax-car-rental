package com.csci4176.halifaxcarrental.chat.item

import android.view.View
import androidx.fragment.app.FragmentManager
import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.chat.PlayAudio
import com.csci4176.halifaxcarrental.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.image_from.view.*

class AudioFromItem(val content_audio: String, val user: String, val fragmentManager: FragmentManager): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val tn = "https://firebasestorage.googleapis.com/v0/b/courseregistration-8ddaa.appspot.com/o/files%2Ftransparent-speaker-icon-29.jpg?alt=media&token=07cbe6f5-185d-4f4b-b314-3a26c809d61e"

        Picasso.get().load(tn).into(viewHolder.itemView.image_content_from)


        if(user != "")
        {
            val uri = user
            val targetImageView = viewHolder.itemView.imageview_chat_from_row

            Picasso.get().load(uri).into(targetImageView)
        }

        viewHolder.itemView.image_content_from?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                val fragment = PlayAudio()

                if(content_audio != "") {
                    val sharedData = Globals.instance
                    sharedData.audioToPlay= content_audio
                    val transaction = fragmentManager.beginTransaction()

                    transaction.replace(R.id.constraintLayout, fragment)
                    transaction.addToBackStack(null)

                    transaction.commit()
                    //view.context.startActivity(intent)
                    //Toast.makeText(view.context, sharedData.videoToPlay , Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    override fun getLayout(): Int {
        return R.layout.image_from
    }
}