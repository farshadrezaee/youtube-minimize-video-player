package ktx

import android.widget.ImageView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.rz.live.R

fun SimpleExoPlayer.bindToPlayPauseImageView(imageView: ImageView) {

    addListener(object : Player.Listener {

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {

            super.onPlayWhenReadyChanged(playWhenReady, reason)

            if (playWhenReady) {
                imageView.setImageResource(R.drawable.exo_controls_pause)
                return
            }

            imageView.setImageResource(R.drawable.exo_controls_play)

        }

    })

    imageView.setOnClickListener {

        if (playWhenReady) {
            pause()
            return@setOnClickListener
        }

        play()

    }

}