package com.csci4176.halifaxcarrental.chat.item

import android.view.View
import androidx.fragment.app.FragmentManager
import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.R
import com.csci4176.halifaxcarrental.chat.WatchVideo
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.image_from.view.*

class VideoFromItem(val content_video: String, val user: String, val fragmentManager: FragmentManager): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val tn = "https://firebasestorage.googleapis.com/v0/b/courseregistration-8ddaa.appspot.com/o/files%2Fplayer-icon-30.png?alt=media&token=d940ef45-7304-4483-b87b-c92cb5749fde"

        Picasso.get().load(tn).into(viewHolder.itemView.image_content_from)


        if(user != "")
        {
            val uri = user
            val targetImageView = viewHolder.itemView.imageview_chat_from_row

            Picasso.get().load(uri).into(targetImageView)
        }

        viewHolder.itemView.image_content_from?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                if(content_video != "") {
                    val fragment = WatchVideo()

                    val sharedData = Globals.instance
                    sharedData.videoToPlay = content_video

                    val transaction = fragmentManager.beginTransaction()

                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)

                    transaction.commit()
                }
            }
        })

    }

    override fun getLayout(): Int {
        return R.layout.image_from
    }
}
