package com.example.halifaxcarrental.chat

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.halifaxcarrental.Globals
import com.example.halifaxcarrental.R
import com.example.halifaxcarrental.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_user_list.*

class ChatListFragment : Fragment() {

    var db = FirebaseFirestore.getInstance()
    val refUsers = db.collection("User")
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_user_list, container, false)
        fragment = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity)!!.supportActionBar?.title = "Chat"

        recyclerview_user_log.adapter = adapter

        getUserss()

        val sharedData = Globals.instance
        if(sharedData.username != "admin")
        {
            sharedData.talkTo = "admin"
            goToChatLog(false)
        }
    }

    companion object {
        lateinit var fragment: Fragment
        fun goToChatLog(addToBackstack: Boolean) {
            val transaction = fragment!!.childFragmentManager.beginTransaction()
            val chatLog: Fragment = ChatLogFragment()
            transaction.replace(R.id.constraintLayout, chatLog)
            if (addToBackstack) {
                transaction.addToBackStack(null)
            }
            transaction.commit()
        }
    }

    fun getUserss() {
        refUsers.get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val userss = documentSnapshot.toObject(User::class.java)
                        if (userss != null) {

                            if(userss.username != "admin")
                            {
                                adapter.add(
                                    ChatList(
                                        userss.username!!,
                                        userss.profileImageUrl!!
                                    )
                                )
                            }

                        }
                    }
                }
            })
    }
}