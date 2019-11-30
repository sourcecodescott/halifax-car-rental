package com.csci4176.halifaxcarrental.chat

import ChatUserItem
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.csci4176.halifaxcarrental.R
import com.csci4176.halifaxcarrental.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*


class LatestMessages : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    private fun fetchUsers() {
        var re :androidx.recyclerview.widget.RecyclerView

        re = recyclerview_newmessage


        val ref = FirebaseDatabase.getInstance().getReference("/User")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(ChatUserItem(user))
                    }
                }



                re.adapter = adapter

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}