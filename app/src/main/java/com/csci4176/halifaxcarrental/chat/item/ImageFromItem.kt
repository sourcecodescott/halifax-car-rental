package com.csci4176.halifaxcarrental.chat.item

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.R
import com.csci4176.halifaxcarrental.ViewImage
import com.csci4176.halifaxcarrental.chat.ChatLogFragment
import com.csci4176.halifaxcarrental.chat.WatchVideo
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.image_from.view.*

class ImageFromItem(val content_image: String, val user: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        if(content_image != "")
        {
            Picasso.get().load(content_image).into(viewHolder.itemView.image_content_from)
        }


        if(user != "")
        {
            val uri = user
            val targetImageView = viewHolder.itemView.imageview_chat_from_row

            Picasso.get().load(uri).into(targetImageView)
        }


        viewHolder.itemView.image_content_from?.setOnClickListener(object: View.OnClickListener {
                    override fun onClick(view: View) {
                        if(content_image != "") {

                            val sharedData = Globals.instance
                            sharedData.photoToView = content_image

                            val intent = Intent(view.context, ViewImage::class.java)
                            view.context.startActivity(intent)

                        }
                    }
                })

    }

    override fun getLayout(): Int {
        return R.layout.image_from
    }
}



