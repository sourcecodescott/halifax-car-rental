package com.csci4176.halifaxcarrental.chat.item

import com.csci4176.halifaxcarrental.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.image_to.view.*

class ImageToItem(val text: String, val user: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        if(text != "") {
            Picasso.get().load(text).into(viewHolder.itemView.image_content_to)
        }


        // load our user image into the star
        if(user != "") {
            val uri = user
            val targetImageView = viewHolder.itemView.imageview_chat_to_row
            Picasso.get().load(uri).into(targetImageView)
        }
    }

    override fun getLayout(): Int {
        return R.layout.image_to
    }
}