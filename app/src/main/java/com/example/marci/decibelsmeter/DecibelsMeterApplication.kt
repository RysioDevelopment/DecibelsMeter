package com.example.marci.decibelsmeter

import android.app.Application
import timber.log.Timber

/**
 ** Created by marci on 2018-06-13.
 */
class DecibelsMeterApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}