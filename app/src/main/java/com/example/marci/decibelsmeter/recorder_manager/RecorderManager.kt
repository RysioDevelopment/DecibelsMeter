package com.example.marci.decibelsmeter.recorder_manager

import android.Manifest
import android.media.MediaRecorder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.Manifest.permission
import android.Manifest.permission.RECORD_AUDIO



/**
 ** Created by marci on 2018-06-13.
 */
class RecorderManager {

  private var mediaRecorder: MediaRecorder? = null
  private var recorderState = RecorderState.STOP
  private var amplitudeDisposable: Disposable? = null
  private val amplitudePublishSubject = PublishSubject.create<Double>()
  private var timerDisposable: Disposable? = null
  private val timerPublishSubject = PublishSubject.create<Int>()
  private var timer = 0
  private var baseValue = 6.0

  fun incrementBaseValue() {
    baseValue++
  }

  fun decrementBaseValue() {
    baseValue--
  }

  fun getRecorderStateName() = recorderState.name

  fun onRecord(filePath: String) {
    if (recorderState == RecorderState.STOP) {
      startRecording(filePath)
      amplitudeDisposable = amplitude()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { decibels ->
            amplitudePublishSubject.onNext(decibels)
          }
      timerDisposable = generateTicks()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { ticks ->
            timerPublishSubject.onNext(ticks)
          }
    } else {
      amplitudeDisposable?.dispose()
      timerDisposable?.dispose()
      stopRecording()
    }
  }

  private fun startRecording(filePath: String) {
    recorderState = RecorderState.RECORDING
    mediaRecorder = MediaRecorder()
    with(mediaRecorder!!) {
      setAudioSource(MediaRecorder.AudioSource.MIC)
      setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
      setOutputFile(filePath)
      setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
      try {
        prepare()
      } catch (e: IOException) {
      }
      start()
    }
  }

  private fun generateTicks(): Observable<Int> {
    return Observable.interval(1, TimeUnit.SECONDS)
        .map { _ ->
          return@map ++timer
        }
  }

  private fun amplitude(): Observable<Double> {
    return Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { _ ->
          /*
                    val amp = (mediaRecorder?.maxAmplitude ?: 0)
                    mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA
                    return@map (-1) * 20 * Math.log10(mEMA / 32767.0)*/
//          return@map (mediaRecorder?.maxAmplitude ?: 0) / 1.0
//          return@map 20 * Math.log10(Math.abs(mediaRecorder?.maxAmplitude!!).toDouble()!!) / 32767.0)
//          return@map 20 * Math.log10((mediaRecorder?.maxAmplitude ?: 0).toDouble())
          val decibels = 20 * Math.log10((mediaRecorder?.maxAmplitude ?: 0).toDouble() / baseValue)
          return@map if (decibels < 0) {
            0.0
          } else {
            decibels
          }
        }
  }

  fun getAmplitudePublishSubject() = amplitudePublishSubject

  fun getTimerPublishSubject() = timerPublishSubject

  private fun stopRecording() {
    if (recorderState == RecorderState.RECORDING) {
      try {
        recorderState = RecorderState.STOP
        mediaRecorder?.stop()
        mediaRecorder?.release()
        timer = 0
        mediaRecorder = null
      } catch (e: IOException) {
        Timber.d(e.localizedMessage)
      }
    }
  }

  fun destroy() {
    amplitudeDisposable?.dispose()
    timerDisposable?.dispose()
    if (mediaRecorder != null) {
      mediaRecorder?.release()
      mediaRecorder = null
    }
  }
}