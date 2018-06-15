package com.example.marci.decibelsmeter.player_manager

import android.media.MediaPlayer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 ** Created by marci on 2018-06-13.
 */
class PlayerManager {

  private var mediaPlayer: MediaPlayer? = null
  private var playerState = PlayerState.PAUSE
  private var disposables: CompositeDisposable = CompositeDisposable()
  private var timerDisposable: Disposable? = null
  private val ticksPublishSubject = PublishSubject.create<Pair<Int, Int>>()

  fun getPlayerStateName() = playerState.name

  fun onPlay(fileName: String) {
    if (playerState == PlayerState.PAUSE) {
      startPlaying(fileName)
      timerDisposable = ticks()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { ticks ->
            ticksPublishSubject.onNext(ticks)
          }
    } else {
      timerDisposable?.dispose()
      stopPlaying()
    }
  }

  private fun startPlaying(fileName: String) {
    mediaPlayer = MediaPlayer()
    try {
      with(mediaPlayer!!) {
        setDataSource(fileName)
        prepare()
        start()
        playerState = PlayerState.PLAY
      }
    } catch (e: IOException) {
      Timber.d(e.localizedMessage)
    }
  }

  private fun ticks(): Observable<Pair<Int, Int>> {
    return Observable.interval(16, TimeUnit.MILLISECONDS)
        .map { _ ->
          val currentPositionInSeconds = (mediaPlayer?.currentPosition ?: 0) / 1000
          val durationInSeconds = (mediaPlayer?.duration ?: 0) / 1000
          return@map Pair(currentPositionInSeconds, durationInSeconds)
        }
  }

  fun getTicksPublishSubject() = ticksPublishSubject

  private fun stopPlaying() {
    if (playerState == PlayerState.PLAY) {
      mediaPlayer!!.release()
      mediaPlayer = null
      playerState = PlayerState.PAUSE
    }
  }

  fun destroy() {
    disposables.dispose()
    if (mediaPlayer != null) {
      mediaPlayer!!.release()
      mediaPlayer = null
    }
  }
}