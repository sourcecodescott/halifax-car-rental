package com.csci4176.halifaxcarrental.chat

import com.csci4176.halifaxcarrental.chat.item.*
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.R
import com.csci4176.halifaxcarrental.User

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat_log.optionsFloatingActionButton
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ChatLogFragment : Fragment() {

    val adapter = GroupAdapter<ViewHolder>()

    var currentPhotoPath: String = ""
    var currentVideoPath: String = ""
    val ref = FirebaseDatabase.getInstance().getReference("/Message")


    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 1

    val REQUEST_VIDEO_CAPTURE = 1

    var takePicture: Boolean = false
    var takeVideo: Boolean = false
    var db = FirebaseFirestore.getInstance()

    var noteRef = db.collection("Message").document()

    val refMessages = db.collection("Message").orderBy("timestamp")

    val refUsers = db.collection("User")

    var to_image: String = ""
    var from_image: String = ""

    var selectedPhotoUri: Uri? = null

    private var showFloatingActions: Boolean = false

    val sharedData = Globals.instance

    var chatLogFragmentManager: FragmentManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_chat_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //(activity!! as AppCompatActivity).supportActionBar?.title = Globals.instance.talkTo
        chatLogFragmentManager = childFragmentManager

        recyclerview_chat_log.adapter = adapter as RecyclerView.Adapter<*>
       // getMessages()
        cameraPermission()
        send_button_chat_log.setOnClickListener {
            if (sharedData.username != null && sharedData.talkTo != null)
                saveTextMessage(sharedData.username!!, sharedData.talkTo!!)
            //Toast.makeText(applicationContext,"Send Button",Toast.LENGTH_SHORT).show()

        }



        send_Image_button.setOnClickListener {

            //showPhoto()
            takePicture = true
            dispatchTakePictureIntent()


        }

        send_audio_button.setOnClickListener {
            val fragment = RecordAudio()
            val transaction = chatLogFragmentManager?.beginTransaction()

            transaction!!.replace(R.id.constraintLayout, fragment)
            transaction!!.addToBackStack(null)

            transaction!!.commit()
        }

        btnrefreshChat.setOnClickListener {
            adapter.clear()
            getMessages()
        }

        send_video_button.setOnClickListener {
            takeVideo = true
            dispatchTakeVideoIntent()
        }

        showHideFloatingActions()
        showPicturePreview(false)
    }

    fun cameraPermission()
    {
        if (ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(activity!!, arrayOf<String>(android.Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
        }
    }


    override fun onStart() {
        super.onStart()
        adapter.clear()
        getMessages()


        optionsFloatingActionButton.setOnClickListener { showHideFloatingActions() }
    }

    // toggle visibility of picture preview
    fun showPicturePreview(show : Boolean)
    {
        if (show)
        {
            thumbnailImageView.visibility = View.VISIBLE
            deleteImageButton.visibility = View.VISIBLE
        }

        else {
            thumbnailImageView.visibility = View.GONE
            deleteImageButton.visibility = View.GONE
        }
    }

    private fun showHideFloatingActions()
    {
        if (showFloatingActions)
        {
           floatingActionsLayout.visibility = View.VISIBLE
        }

        else
        {
            floatingActionsLayout.visibility = View.GONE
        }

        showFloatingActions = !showFloatingActions
    }



    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    //Image String =... ........ it.toString())
                    if (sharedData.username != null && sharedData.talkTo != null)
                        saveImageMessage(it.toString(),sharedData.username!!, sharedData.talkTo!!)


                }
            }
            .addOnFailureListener {
            }
    }


    private fun uploadVideoToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/videos/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    //Image String =... ........ it.toString())
                    if (sharedData.username != null && sharedData.talkTo != null)
                        saveVideoMessage(it.toString(),sharedData.username!!, sharedData.talkTo!!)
                    Toast.makeText(activity!!, "Successfully Uploaded", Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener {
                //Toast.makeText(this@ChatLogFragment, "Failed Uploaded", Toast.LENGTH_SHORT).show()
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && takePicture == true) {
            uploadImageToFirebaseStorage()

            takePicture = false
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && takeVideo == true) {

            Toast.makeText(activity!!, "Video: "+data?.data.toString(), Toast.LENGTH_SHORT).show()
            selectedPhotoUri = data?.data
            uploadVideoToFirebaseStorage()

            takeVideo = false
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file =  File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = file.absolutePath
        return file
    }

    private fun dispatchTakeVideoIntent() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideoIntent.resolveActivity(activity!!.packageManager)
        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        val resolveActivity = takePictureIntent.resolveActivity(activity!!.packageManager)
        // Create the File where the photo should go
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            null
        }

        val photoURI: Uri = FileProvider.getUriForFile(
            activity!!,
            "com.csci4176.halifaxcarrental.fileprovider",
            photoFile!!
        )

        selectedPhotoUri = photoURI
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
    }

    fun saveVideoMessage(url:String, fromName:String, to_Name:String) {

        var myRef = db.collection("Message").document()

        var ccc = ChatMessage(
            UUID.randomUUID().toString(),
            url,
            fromName,
            to_Name,
            System.currentTimeMillis() / 1000,true,"video"
        )
        myRef.set(ccc).addOnSuccessListener {

            edittext_chat_log.setText("")

            adapter.clear()
            getMessages()
        }

    }

    fun saveImageMessage(url:String, fromName:String, to_Name:String) {

        var myRef = db.collection("Message").document()

        var ccc = ChatMessage(
            UUID.randomUUID().toString(),
            url,
            fromName,
            to_Name,
            System.currentTimeMillis() / 1000,true,"picture"
        )
        myRef.set(ccc).addOnSuccessListener {

            edittext_chat_log.setText("")

            adapter.clear()
            getMessages()
        }

    }

    fun showPhoto()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }


    fun getMessages() {
        getUser(sharedData.talkTo!!, sharedData.username!!)

        refMessages.get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val chatMessage = documentSnapshot.toObject(ChatMessage::class.java)
                        if (chatMessage != null) {

                            if (chatMessage.from_person== sharedData.username && chatMessage.to_person == sharedData.talkTo ) {

                               if(chatMessage.format == "picture")
                                {
                                    adapter.add(ImageFromItem(chatMessage.text, from_image))
                                }
                               else if(chatMessage.format == "text")
                                  {
                                      adapter.add(ChatFromItem(chatMessage.text, from_image))
                                  }
                                else if(chatMessage.format == "video")
                               {
                                   adapter.add(VideoFromItem(chatMessage.text, from_image, activity!!.supportFragmentManager))
                               }

                               else if(chatMessage.format == "audio") {

                                   adapter.add(AudioFromItem(chatMessage.text, from_image, chatLogFragmentManager!!))
                               }


                               } else if (chatMessage.from_person== sharedData.talkTo && chatMessage.to_person == sharedData.username ) {

                             if(chatMessage.format == "picture")
                                {
                                    adapter.add(ImageToItem(chatMessage.text, to_image))
                                } else if(chatMessage.format == "text")
                               {
                                   adapter.add(ChatToItem(chatMessage.text, to_image))
                               }
                                else if(chatMessage.format == "video")
                             {
                                 adapter.add(VideoToItem(chatMessage.text, to_image, chatLogFragmentManager!!))
                             }
                             else if(chatMessage.format == "audio") {

                                 adapter.add(AudioToItem(chatMessage.text, to_image, chatLogFragmentManager!!))
                             }

                            }
                        }
                    }
                }
            })
    }

    fun getUser(to_username: String, from_username: String) {

        refUsers.get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val user = documentSnapshot.toObject(User::class.java)
                        if (user != null) {

                            if (user.username == to_username) {
                                to_image = user.profileImageUrl!!
                            }

                            if (user.username == from_username) {
                                from_image = user.profileImageUrl!!
                            }
                        }
                    }
                }
            })

    }


    fun saveTextMessage(fromName:String, to_Name:String) {

        var myRef = db.collection("Message").document()

        var ccc = ChatMessage(
            UUID.randomUUID().toString(),
            edittext_chat_log.text.toString(),
            fromName,
            to_Name,
            System.currentTimeMillis() / 1000,false,"text"
        )
        myRef.set(ccc).addOnSuccessListener {

            edittext_chat_log.setText("")

            adapter.clear()
            getMessages()
        }

    }
}