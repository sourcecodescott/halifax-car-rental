package com.example.halifaxcarrental.chat.item

import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.halifaxcarrental.Globals
import com.example.halifaxcarrental.R
import com.example.halifaxcarrental.chat.WatchVideo
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.image_to.view.*

class VideoToItem(val videocontent: String, val user: String, val fragmentManager: FragmentManager): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val tn = "https://firebasestorage.googleapis.com/v0/b/courseregistration-8ddaa.appspot.com/o/files%2Fplayer-icon-30.png?alt=media&token=d940ef45-7304-4483-b87b-c92cb5749fde"

        Picasso.get().load(tn).into(viewHolder.itemView.image_content_to)

        // load our user image into the star
        if(user != "") {
            val uri = user
            val targetImageView = viewHolder.itemView.imageview_chat_to_row
            Picasso.get().load(uri).into(targetImageView)
        }

        setListenerOnVideoClick(viewHolder)
    }

    fun setListenerOnVideoClick(viewHolder: ViewHolder) {
        viewHolder.itemView.image_content_to?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                startWatchVideo()
            }
        })
    }

    fun startWatchVideo() {
        val fragment = WatchVideo()

        if(videocontent != "") {
            val sharedData = Globals.instance
            sharedData.videoToPlay = videocontent

            val transaction = fragmentManager.beginTransaction()

            transaction.replace(R.id.constraintLayout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getLayout(): Int {
        return R.layout.image_to
    }
}