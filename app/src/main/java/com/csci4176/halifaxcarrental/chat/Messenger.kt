package com.csci4176.halifaxcarrental.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import com.csci4176.halifaxcarrental.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_messenger.*
import java.util.*


class Messenger : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)


        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }


    fun showPhoto(v: View?)
    {


        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)

    }


    override fun onStart() {
        super.onStart()

    }

    var selectedPhotoUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                        //Image String =... ........ it.toString())

                }
            }
            .addOnFailureListener {
            }
    }


}
