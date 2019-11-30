package com.csci4176.halifaxcarrental.chat

import android.view.View
import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.car_list_control.view.*

class ChatList(val userName: String, val userImage: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtName.text = userName

        val uri = userImage
        val targetImageView = viewHolder.itemView.img_Car_Img
        Picasso.get().load(uri).into(targetImageView)

        viewHolder.itemView.img_Car_Img?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                setUser()

                ChatListFragment.goToChatLog(true)
            }
        })

        viewHolder.itemView.txtName?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                setUser()
                ChatListFragment.goToChatLog(true)
            }
        })
    }

    fun setUser() {
        val sharedData = Globals.instance
        sharedData.talkTo = userName
    }

    override fun getLayout(): Int {
        return R.layout.car_list_control
    }
}