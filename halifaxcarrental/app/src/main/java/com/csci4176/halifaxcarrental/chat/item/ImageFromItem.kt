package com.csci4176.halifaxcarrental.chat.item

import com.csci4176.halifaxcarrental.R
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

    }

    override fun getLayout(): Int {
        return R.layout.image_from
    }
}