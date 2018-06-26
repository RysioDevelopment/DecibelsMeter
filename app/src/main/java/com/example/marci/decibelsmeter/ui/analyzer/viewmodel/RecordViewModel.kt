package com.example.marci.decibelsmeter.ui.analyzer.viewmodel

/**
 ** Created by marci on 2018-06-24.
 */
data class RecordViewModel(
    val mainRecordName: String,
    val leftSpeakerDecibels: Int? = 0,
    val rightSpeakerDecibels: Int? = 0
//    val setUpSpeakersStatement: String
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
          else -> "Well set loudspeakers"
        }
      }
    }
  }
}