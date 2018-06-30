package com.example.marci.decibelsmeter.ui.recording

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.marci.decibelsmeter.R
import com.example.marci.decibelsmeter.recorder_manager.RecorderManager
import com.example.marci.decibelsmeter.recorder_manager.RecorderState
import com.example.marci.decibelsmeter.shared_prefs.PrefsManager
import com.example.marci.decibelsmeter.utils.DateUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_recording.*
import kotlinx.android.synthetic.main.recording_layout.view.*
import org.joda.time.DateTime

/**
 * * Created by marci on 2018-06-09.
 */

class RecordingFragment : Fragment() {

  private lateinit var fileName: String
  private var recorderManager = RecorderManager()
  // Requesting permission to RECORD_AUDIO
  private var permissionToRecordAccepted = false
  private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
  private val REQUEST_RECORD_AUDIO_PERMISSION = 200
  private val disposables: CompositeDisposable = CompositeDisposable()
  lateinit var prefsManager: PrefsManager

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
    if (!permissionToRecordAccepted) {
      activity.finish()
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_recording, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    ActivityCompat.requestPermissions(activity, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
    prefsManager = PrefsManager(activity.application)
    recordNameEditText.text = Editable.Factory.getInstance().newEditable(DateTime.now().toString().replace(":", "_"))
    gaugeView.setShowRangeValues(true)
    decibelsTextView.visibility = View.GONE
    setUpLeftSpeakerRecordingLayout()
    setUpRightSpeakerRecordingLayout()
    setUpDecibelsMeter()
    setUpRecorderTimer()
    setUpOptimizeButtons()
  }

  private fun setUpLeftSpeakerRecordingLayout() {
    leftRecordingLayout.speakerTextView.text = getString(R.string.left_speaker)
    leftRecordingLayout.recordingButton.setOnClickListener {
      if (!rightRecordingLayout.recordingButton.isSelected) {
        setUpRecordingButton(leftRecordingLayout.saveButton, getString(R.string.left_speaker_shortcut))
        it.isSelected = !it.isSelected
      } else {
        Toast.makeText(context, "Firstly stop recording other speaker", Toast.LENGTH_SHORT).show()
      }
    }
    setUpSaveButtonListener(leftRecordingLayout, getString(R.string.left_speaker_shortcut))
  }

  private fun setUpRightSpeakerRecordingLayout() {
    rightRecordingLayout.speakerTextView.text = getString(R.string.right_speaker)
    rightRecordingLayout.recordingButton.setOnClickListener {
      if (!leftRecordingLayout.recordingButton.isSelected) {
        setUpRecordingButton(rightRecordingLayout.saveButton, getString(R.string.right_speaker_shortcut))
        it.isSelected = !it.isSelected
      } else {
        Toast.makeText(context, "Firstly stop recording other speaker", Toast.LENGTH_SHORT).show()
      }
    }
    setUpSaveButtonListener(rightRecordingLayout, getString(R.string.right_speaker_shortcut))
  }

  private fun setUpRecordingButton(saveButton: Button, speaker: String) {
    saveButton.isEnabled = true
    fileName = activity.externalCacheDir.absolutePath
    fileName += "/${recordNameEditText.text}$speaker.3gp"
    recorderManager.onRecord(fileName)
    actionTextView.text = recorderManager.getRecorderStateName()
  }


  private fun setUpSaveButtonListener(recordView: View, speaker: String) {
    recordView.saveButton.setOnClickListener {
      val mainFileName = recordNameEditText.text.toString()
      if (recordNameEditText.text.isNotEmpty()) {
        prefsManager.createPreferences(mainFileName).put(mainFileName + speaker, decibelsTextView.text)
        it.isEnabled = false
        recordView.recordingButton.isSelected = false
        if (recorderManager.getRecorderStateName() == RecorderState.RECORDING.name) {
          recorderManager.onRecord(mainFileName + speaker)
        }
        timerTextView.text = getString(R.string.start_time)
        decibelsTextView.text = getString(R.string.decibels, 0)
        Toast.makeText(activity.baseContext, "Saved sound", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(activity.baseContext, "Firstly name your file", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun setUpDecibelsMeter() {
    val disposable = recorderManager.getAmplitudePublishSubject()
        .subscribe { decibels ->
          val dB = decibels.toInt()
          decibelsTextView.text = "$dB"
          gaugeView.setTargetValue(dB.toFloat())
          when {
            dB < 10 -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_barely_audible)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(activity.baseContext, R.color.colorRed))
            }
            dB < 35 -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_quiet)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(activity.baseContext, R.color.colorOrange))
            }
            dB < 55 -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_perfectly_audible)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(activity.baseContext, R.color.colorGreen))
            }
            dB < 60 -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_loud)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(activity.baseContext, R.color.colorOrange))
            }
            else -> {
              dBInterpretationTextView.text = getString(R.string.the_music_is_too_loud)
              dBInterpretationTextView.setTextColor(ContextCompat.getColor(activity.baseContext, R.color.colorRed))
            }
          }
        }
    disposables.add(disposable)
  }

  private fun setUpRecorderTimer() {
    val disposable = recorderManager.getTimerPublishSubject()
        .subscribe { ticks ->
          timerTextView.text = DateUtils.secondsToTimeString((ticks))
        }
    disposables.add(disposable)
  }

  private fun setUpOptimizeButtons() {
    increaseDecibelsButton.setOnClickListener {
      recorderManager.decrementBaseValue()
    }
    lowerDecibelsButton.setOnClickListener {
      recorderManager.incrementBaseValue()
    }
  }

  override fun onStop() {
    super.onStop()
    disposables.dispose()
    recorderManager.destroy()
  }
}
