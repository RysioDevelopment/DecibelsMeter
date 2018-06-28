package com.example.marci.decibelsmeter.ui.analyzer.viewmodel

import com.example.marci.decibelsmeter.R

/**
 ** Created by marci on 2018-06-24.
 */
data class RecordViewModel(
    val mainRecordName: String,
    val leftSpeakerDecibels: Int? = 0,
    val rightSpeakerDecibels: Int? = 0,
    var imageView: Int = R.drawable.ic_close
) {

  fun getStatement(): String {
    return when {
      leftSpeakerDecibels == null -> "Missing left record"
      rightSpeakerDecibels == null -> "Missing right record"
      else -> {
        val decibelsDifference = leftSpeakerDecibels - rightSpeakerDecibels
        when {
          decibelsDifference > 7 -> "Right speaker is too far"
          decibelsDifference < -7 -> "Left speaker is too far"
          else -> {
            imageView = R.drawable.ic_done
            "Well set loudspeakers"
          }
        }
      }
    }
  }
}