package com.example.halifaxcarrental.chat

import android.Manifest
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
import kotlinx.android.synthetic.main.activity_play_audio.*
import java.io.IOException


class PlayAudio : Fragment() {


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
            parentFragment!!.childFragmentManager.popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_play_audio, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnplayMe.isEnabled = true
        btnstopMe  .isEnabled = false

        // Record to the external cache directory for visibility
        audioPermission()

        btnplayMe.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                startPlaying()
                btnplayMe.isEnabled = false
                btnstopMe  .isEnabled = true
            }
        })


        btnstopMe.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                stopPlaying()

                btnplayMe.isEnabled = true
                btnstopMe  .isEnabled = false
            }
        })

        btnReturnMe.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                /*
                val fragment = ChatLogFragment()
                val transaction = childFragmentManager.beginTransaction()

                transaction.replace(R.id.constraintLayout, fragment)
                transaction.addToBackStack(null)

                transaction.commit()*/
                parentFragment!!.childFragmentManager.popBackStack()
            }
        })

        val sharedData = Globals.instance

        fileName = sharedData.audioToPlay!!

    }

    fun startPlaying() {
        player = MediaPlayer()
        try {
            player?.setDataSource(fileName)
            //Toast.makeText(this@PlayAudio, fileName, Toast.LENGTH_SHORT).show()
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
