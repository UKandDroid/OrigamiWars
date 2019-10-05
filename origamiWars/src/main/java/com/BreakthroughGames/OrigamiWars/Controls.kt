package com.BreakthroughGames.OrigamiWars

import android.content.Context
import android.view.GestureDetector
import android.view.View
import android.view.MotionEvent



class Controls {
    private lateinit var screeView : View
    private lateinit var mDetector: GestureDetector
    private lateinit var gesture: Gesture
    private fun setScreen(view : View){
        screeView = view
    }

    fun initialize(context: Context, view : View){
        setScreen(view)
        mDetector = GestureDetector(context, Gesture())
        screeView.setOnTouchListener { view: View, motionEvent: MotionEvent -> run {
            mDetector.onTouchEvent(motionEvent)
        }
            return@setOnTouchListener true
        }
    }


    internal inner class Gesture : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(ev: MotionEvent): Boolean { return true }
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean { return true}
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return super.onSingleTapConfirmed(e)
        }
    }
}