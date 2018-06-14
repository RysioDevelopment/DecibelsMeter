package com.example.marci.decibelsmeter.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.marci.decibelsmeter.R
import com.example.marci.decibelsmeter.player_manager.PlayerManager
import com.example.marci.decibelsmeter.recorder_manager.RecorderManager
import com.example.marci.decibelsmeter.utils.DateUtils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * * Created by marci on 2018-06-09.
 */

class MainActivity : AppCompatActivity() {

  private lateinit var fileName: String
  private var recorderManager = RecorderManager()
  private var playerManager = PlayerManager()

  public override fun onCreate(bundle: Bundle?) {
    super.onCreate(bundle)
    setContentView(R.layout.activity_main)
    fileName = externalCacheDir.absolutePath
    fileName += "/audiorecordtest.3gp"
    playerManager.getTicksPublishSubject()
        .subscribe { ticks ->
          timerTextView.text = DateUtils.secondsToTimeString((ticks.first))
        }
    recorderManager.getAmplitudePublishSubject()
        .subscribe { amplitude ->
          decibelsTextView.text = "${amplitude.toInt()}[dB]"
        }
    recorderManager.getTimerPublishSubject()
        .subscribe { ticks ->
          timerTextView.text = DateUtils.secondsToTimeString((ticks))
        }
    recordingButton.setOnClickListener {
      if (!playButton.isSelected) {
        recorderManager.onRecord(fileName)
        recordingButton.isSelected = !recordingButton.isSelected
        actionTextView.text = recorderManager.getRecorderStateName()
      } else {
        Toast.makeText(baseContext, "Firstly stop playing!", Toast.LENGTH_SHORT).show()
      }
    }
    playButton.setOnClickListener {
      if (!recordingButton.isSelected) {
        playerManager.onPlay(fileName)
        playButton.isSelected = !playButton.isSelected
        actionTextView.text = playerManager.getPlayerStateName()
      } else {
        Toast.makeText(baseContext, "Firstly stop recording!", Toast.LENGTH_SHORT).show()
      }
    }
  }

  public override fun onStop() {
    super.onStop()
    recorderManager.destroy()
    playerManager.destroy()
  }
}
