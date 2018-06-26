package com.example.marci.decibelsmeter.shared_prefs

import android.app.Application
import android.content.Context
import java.io.File

/**
 ** Created by marci on 2018-06-22.
 */
class PrefsManager(
    private val application: Application
) {

  fun createPreferences(preferenceFile: String = DEFAULT_PREFS_FILE) =
      PrefsStorage(application.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE))

  fun getAllFiles(): Array<String>? {
    val prefsDir = File(application.applicationInfo.dataDir, "shared_prefs")
    if (prefsDir.exists() && prefsDir.isDirectory) {
      return prefsDir.list()
    }
    return null
  }

  companion object {
    const val DEFAULT_PREFS_FILE: String = "default_prefs"
    const val SUBSCRIBED_SERIES = "subscribed_series_prefs"
    const val SERIES_SETTINGS = "series_settings"
  }
}