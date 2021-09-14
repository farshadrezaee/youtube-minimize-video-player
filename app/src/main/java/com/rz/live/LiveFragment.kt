package com.rz.live

import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.rz.live.databinding.FragmentLiveBinding
import ktx.bindToPlayPauseImageView

/**
 * Created by Farshad Rezaei.
 *
 * Email: farshad7srt@gmail.com
 */

class LiveFragment : Fragment(R.layout.fragment_live) {

    private var _binding: FragmentLiveBinding? = null
    private val binding get() = _binding!!

    private val playerManager: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(requireContext())
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(requireContext())
            )
            .build()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentLiveBinding.bind(requireView())

        preparePlayer()

        initViews()

    }

    private fun initViews() {

        binding.apply {

            motionLayout.transitionToEnd()

            playerView.player = playerManager

            closeImageView.setOnClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(0, R.anim.slide_in_down)
                    .remove(this@LiveFragment)
                    .commit()
            }

        }

    }

    private fun preparePlayer() {

        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri("https://moctobpltc-i.akamaihd.net/hls/live/571329/eight/playlist.m3u8")
            .setLiveMaxPlaybackSpeed(1.02f)
            .build()

        playerManager.setMediaItem(mediaItem)
        playerManager.bindToPlayPauseImageView(binding.playPauseImageView)
        playerManager.addAnalyticsListener(object : AnalyticsListener {

            override fun onSurfaceSizeChanged(
                eventTime: AnalyticsListener.EventTime,
                width: Int,
                height: Int
            ) {
                super.onSurfaceSizeChanged(eventTime, width, height)

                val surfaceView = binding.playerView.videoSurfaceView

                if (surfaceView is SurfaceView) {
                    surfaceView.holder.setFixedSize(width, height)
                }

            }

        })

        playerManager.prepare()
        playerManager.play()

    }

    override fun onStop() {
        playerManager.pause()
        binding.playerView.showController()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        playerManager.release()
        super.onDestroy()
    }

}