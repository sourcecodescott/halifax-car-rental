package com.csci4176.halifaxcarrental.car

import android.content.Intent
import android.view.View
import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.cardview_car_small.view.*

class CarList(val car: Car): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtName.text = car.name
        viewHolder.itemView.txtMake.text = car.make
        viewHolder.itemView.txtModel.text = car.model
        if (car.isavaliable)
            viewHolder.itemView.txtAvailable.text = "Available"
        else
            viewHolder.itemView.txtAvailable.text = "Not Available"
        viewHolder.itemView.txtFee.text = ("$" + car.price)


        // load our user image into the star
        val uri = car.car_image
        val targetImageView = viewHolder.itemView.img_Car_Img
        Picasso.get().load(uri).into(targetImageView)

        viewHolder.itemView.img_Car_Img?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {

                //Toast.makeText(view.context, "Login Successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(view.context, CarDetails::class.java)

                val sharedData = Globals.instance
                sharedData.car_name = car.name
                view.context.startActivity(intent)
            }
        })

        viewHolder.itemView.txtName?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {

                //Toast.makeText(view.context, "Login Successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(view.context, CarDetails::class.java)

                val sharedData = Globals.instance
                sharedData.car_name = car.name
                view.context.startActivity(intent)
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.cardview_car_small
    }
}