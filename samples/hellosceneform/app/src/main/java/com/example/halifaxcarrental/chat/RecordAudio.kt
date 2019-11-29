package com.example.halifaxcarrental.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.halifaxcarrental.Globals
import com.example.halifaxcarrental.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_record_audio.*
import java.io.File
import java.io.IOException
import java.util.*

class RecordAudio : Fragment() {


    private  val LOG_TAG = "AudioRecordTest"
    private  val REQUEST_RECORD_AUDIO_PERMISSION = 200
    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    private var fileName: String = ""

    var selectedPhotoUri: Uri? = null

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null

    var db = FirebaseFirestore.getInstance()

    var mStartRecording = true

    var text = ""
    var mStartPlaying = true

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted)
            activity!!.fragmentManager.popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_record_audio, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        play.isEnabled = false
        stop.isEnabled = false
        btnStop_Play.isEnabled = false
        btnsendAudio.isEnabled = false

        // Record to the external cache directory for visibility
        audioPermission()

        //ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        val sharedData = Globals.instance

        btnsendAudio.text = "Send Recording to "+ sharedData.talkTo

        record.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                fileName = "${activity!!.externalCacheDir?.absolutePath}/${UUID.randomUUID().toString()}.3gp"

                stop.isEnabled = true
                record.isEnabled = false

                play.isEnabled = false
                btnStop_Play   .isEnabled = false

                btnsendAudio.isEnabled = false
                startRecording()
            }
        })


        stop.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                stopRecording()

                stop.isEnabled = false
                record.isEnabled = true
                play.isEnabled = true

                btnsendAudio.isEnabled = true
            }
        })

        play.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                startPlaying()
                play.isEnabled = false
                 btnStop_Play   .isEnabled = true
            }
        })


        btnStop_Play.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                stopPlaying()

                play.isEnabled = true
                btnStop_Play   .isEnabled = false
            }
        })


        btnsendAudio.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                selectedPhotoUri = Uri.fromFile(File(fileName))
                uploadAudioToFirebaseStorage()
            }
        })


        btnreturntoChat.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                val fragment = ChatLogFragment()
                val transaction = childFragmentManager.beginTransaction()

                transaction.replace(R.id.constraintLayout, fragment)
                transaction.addToBackStack(null)

                transaction.commit()
            }
        })

    }



    private fun uploadAudioToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/audios/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    val sharedData = Globals.instance
                    saveAudioMessage(it.toString(),sharedData.username!!, sharedData.talkTo!!)
                    //Toast.makeText(this@Home, "Successfully Uploaded", Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener {
                //Toast.makeText(this@Home, "Fail Uploaded", Toast.LENGTH_SHORT).show()
            }
    }




    fun saveAudioMessage(url:String, fromName:String, to_Name:String) {

        var myRef = db.collection("Message").document()

        var ccc = ChatMessage(
            UUID.randomUUID().toString(),
            url,
            fromName,
            to_Name,
            System.currentTimeMillis() / 1000, false, "audio"
        )
        myRef.set(ccc).addOnSuccessListener {
            val intent = Intent(activity!!, ChatLogFragment::class.java)
            startActivity(intent)

        }

    }






    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer()
        try {
            player?.setDataSource(fileName)
            //Toast.makeText(this@RecordAudio, fileName, Toast.LENGTH_SHORT).show()
            player?.prepare()
            player?.start()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder?.setOutputFile(fileName)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            recorder?.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }

        recorder?.start()
    }

    private fun stopRecording() {
        recorder?.stop()
        recorder?.release()
        recorder = null
    }


    fun audioPermission()
    {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(activity!!, arrayOf<String>(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }


}
