package com.example.halifaxcarrental.chat.item

import com.example.halifaxcarrental.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*

class ChatFromItem(val text: String, val user: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        if(user != "") {
            val uri = user
            val targetImageView = viewHolder.itemView.imageview_chat_from_row
            Picasso.get().load(uri).into(targetImageView)
        }

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}