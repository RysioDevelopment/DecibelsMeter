package com.example.marci.decibelsmeter.utils

import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

/**
 ** Created by marci on 2018-06-13.
 */
object DateUtils {

  const val TIME_PATTERN = "HH:mm:ss"
  const val REFERENCE_TIME = "00:00:00"
  private val formatter = SimpleDateFormat(TIME_PATTERN, Locale.getDefault())

  fun stringTimeToSeconds(time: String): Long {
    return (formatter.parse(time).time - formatter.parse("00:00:00").time) / 1000L
  }

  fun secondsToTimeString(seconds: Int): String {
    return DateTime(((seconds - 3600) * 1000).toLong()).toString(TIME_PATTERN)
  }
}