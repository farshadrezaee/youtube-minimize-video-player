package com.rz.live

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.exoplayer2.ui.PlayerView

/**
 * Created by Farshad Rezaei.
 *
 * Email: farshad7srt@gmail.com
 */

class PlayerScreenMotionLayout(
    context: Context,
    attributeSet: AttributeSet? = null
) : MotionLayout(context, attributeSet) {

    private val viewToDetectTouch by lazy {
        findViewById<View>(R.id.playerBackgroundView)
    }
    private val playerView by lazy {
        findViewById<PlayerView>(R.id.playerView)
    }

    private val viewRect = Rect()
    private var hasTouchStarted = false
    private val transitionListenerList = mutableListOf<TransitionListener?>()

    init {

        super.setTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(
                p0: MotionLayout?,
                p1: Int,
                p2: Boolean,
                p3: Float
            ) {
            }

            override fun onTransitionStarted(
                p0: MotionLayout?,
                p1: Int,
                p2: Int
            ) {
                playerView.useController = false
                playerView.hideController()
            }

            override fun onTransitionChange(
                p0: MotionLayout?,
                p1: Int,
                p2: Int,
                p3: Float
            ) {
                transitionListenerList.filterNotNull()
                    .forEach { it.onTransitionChange(p0, p1, p2, p3) }

                if (p1 == p0?.startState &&
                    p2 == p0.endState
                ) {
                    playerView.useController = false
                    if (playerView.isControllerVisible) {
                        playerView.hideController()
                    }
                }


            }

            override fun onTransitionCompleted(
                p0: MotionLayout?,
                p1: Int
            ) {

                transitionListenerList.filterNotNull()
                    .forEach { it.onTransitionCompleted(p0, p1) }

                if (p1 == p0?.endState) {
                    playerView.useController = true
                }

                if (p1 == p0?.startState) {
                    playerView.useController = false
                    playerView.hideController()
                }

                hasTouchStarted = false

            }
        })

    }

    override fun setTransitionListener(listener: TransitionListener?) {
        addTransitionListener(listener)
    }

    override fun addTransitionListener(listener: TransitionListener?) {
        transitionListenerList += listener
    }

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(event: MotionEvent?): Boolean {

                val closeRect =  Rect()
                val closeImageView = findViewById<View>(R.id.closeImageView)
                closeImageView.getHitRect(closeRect)
                val hasTouchCloseImageView =
                    closeRect.contains(event?.x?.toInt() ?: 0, event?.y?.toInt() ?: 0)

                val playPauseRect = Rect()
                val playPauseImageView = findViewById<View>(R.id.playPauseImageView)
                playPauseImageView.getHitRect(playPauseRect)
                val hasTouchPlayPauseView =
                    playPauseRect.contains(event?.x?.toInt() ?: 0, event?.y?.toInt() ?: 0)

                if (!hasTouchCloseImageView && !hasTouchPlayPauseView) {
                    transitionToEnd()
                }

                return false
            }
        })


    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)   //This ensures the Mini Player is maximised on single tap

        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                hasTouchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (!hasTouchStarted) {
            viewToDetectTouch.getHitRect(viewRect)
            hasTouchStarted = viewRect.contains(event.x.toInt(), event.y.toInt())
        }
        return hasTouchStarted && super.onTouchEvent(event)
    }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return !onTouchEvent((event))
    }

}