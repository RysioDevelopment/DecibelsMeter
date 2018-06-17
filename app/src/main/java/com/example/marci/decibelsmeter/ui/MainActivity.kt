package com.example.marci.decibelsmeter.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
  // Requesting permission to RECORD_AUDIO
  private var permissionToRecordAccepted = false
  private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
  private val REQUEST_RECORD_AUDIO_PERMISSION = 200

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
    if (!permissionToRecordAccepted) {
      finish()
    }
  }

  public override fun onCreate(bundle: Bundle?) {
    super.onCreate(bundle)
    setContentView(R.layout.activity_main)
    ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
    gaugeView.setShowRangeValues(true)
    gaugeView.setTargetValue(0f)
    fileName = externalCacheDir.absolutePath
    fileName += "/audiorecordtest.3gp"
    setUpPlayerTimer()
    setUpDecibelsMeter()
    setUpRecorderTimer()
    setUpRecordButtonListener()
    setUpPlayButtonListener()
    increaseDecibelsButton.setOnClickListener {
      recorderManager.decrementBaseValue()
    }
    lowerDecibelsButton.setOnClickListener {
      recorderManager.incrementBaseValue()
    }
  }

  private fun setUpPlayButtonListener() {
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

  private fun setUpRecordButtonListener() {
    recordingButton.setOnClickListener {
      if (!playButton.isSelected) {
        recorderManager.onRecord(fileName)
        recordingButton.isSelected = !recordingButton.isSelected
        actionTextView.text = recorderManager.getRecorderStateName()
      } else {
        Toast.makeText(baseContext, "Firstly stop playing!", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun setUpRecorderTimer() {
    recorderManager.getTimerPublishSubject()
        .subscribe { ticks ->
          timerTextView.text = DateUtils.secondsToTimeString((ticks))
        }
  }

  private fun setUpDecibelsMeter() {
    recorderManager.getAmplitudePublishSubject()
        .subscribe { decibels ->
          val dB = decibels.toInt()
//          decibelsTextView.text = getString(R.string.decibels, dB)
          gaugeView.setTargetValue(dB.toFloat())
          when {
            dB < 10 -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_barely_audible)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(baseContext, R.color.colorRed))
            }
            dB < 35 -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_quiet)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(baseContext, R.color.colorOrange))
            }
            dB < 55 -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_perfectly_audible)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(baseContext, R.color.colorGreen))
            }
            dB < 60 -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_loud)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(baseContext, R.color.colorOrange))
            }
            else -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_too_loud)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(baseContext, R.color.colorRed))
            }
          }
        }
  }

  private fun setUpPlayerTimer() {
    playerManager.getTicksPublishSubject()
        .subscribe { ticks ->
          timerTextView.text = DateUtils.secondsToTimeString((ticks.first))
        }
  }

  public override fun onStop() {
    super.onStop()
    recorderManager.destroy()
    playerManager.destroy()
  }
}
