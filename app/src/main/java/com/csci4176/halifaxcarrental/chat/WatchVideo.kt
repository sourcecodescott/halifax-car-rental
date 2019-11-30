package com.csci4176.halifaxcarrental.chat

import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_watch_video.*
import android.widget.MediaController
import androidx.activity.OnBackPressedCallback
import com.csci4176.halifaxcarrental.Globals
import com.csci4176.halifaxcarrental.R

class WatchVideo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_watch_video, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateVideoPlayer()
        playVideo()

        requireActivity().onBackPressedDispatcher.addCallback(this, object:OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                stopVideo()
                fragmentManager!!.popBackStack()
            }
        })
    }

    fun initiateVideoPlayer() {
        val mediaController = MediaController(activity!!)
        mediaController.setAnchorView(vioPlaytMe)
        vioPlaytMe.setMediaController(mediaController)

        val sharedData = Globals.instance

        var uri = Uri.parse(sharedData.videoToPlay)
        vioPlaytMe.setVideoURI(uri)
    }

    fun playVideo() {
        vioPlaytMe.start()
    }

    fun isPlayingVideo() : Boolean {
        return vioPlaytMe.isPlaying
    }

    fun stopVideo() {
        vioPlaytMe.stopPlayback()
    }

    override fun onStart() {
        super.onStart()

        initiateVideoPlayer()
        playVideo()
    }
}
