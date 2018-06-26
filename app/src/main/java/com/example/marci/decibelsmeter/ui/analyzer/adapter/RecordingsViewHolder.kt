package com.example.marci.decibelsmeter.ui.analyzer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.marci.decibelsmeter.R
import com.example.marci.decibelsmeter.player_manager.PlayerManager
import com.example.marci.decibelsmeter.ui.analyzer.viewmodel.RecordViewModel
import kotlinx.android.synthetic.main.item_record.view.*

/**
 ** Created by marci on 2018-06-22.
 */
class RecordingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  fun bind(record: RecordViewModel, playerManager: PlayerManager) {
    with(itemView) {
      var fileName = context.externalCacheDir.absolutePath
      recordMainNameTextView.text = record.mainRecordName
      leftSpeakerDecibelsTextView.text = context.getString(
          R.string.decibels,
          record.leftSpeakerDecibels ?: 0
      )
      rightSpeakerDecibelsTextView.text = context.getString(
          R.string.decibels,
          record.rightSpeakerDecibels ?: 0
      )
      setUpSpeakersStatementTextView.text = record.getStatement()
      leftPlayButton.setOnClickListener {
        if (!rightPlayButton.isSelected) {
          fileName += "/${record.mainRecordName}${context.getString(R.string.left_speaker_shortcut)}.3gp"
          playerManager.onPlay(fileName)
          it.isSelected = !it.isSelected
        } else {
          Toast.makeText(itemView.context, "Firstly stop other playing!", Toast.LENGTH_SHORT).show()
        }
      }
      rightPlayButton.setOnClickListener {
        if (!leftPlayButton.isSelected) {
          fileName += "/${record.mainRecordName}${context.getString(R.string.right_speaker_shortcut)}.3gp"
          playerManager.onPlay(fileName)
          it.isSelected = !it.isSelected
        } else {
          Toast.makeText(itemView.context, "Firstly stop other playing!", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  companion object {
    fun create(parent: ViewGroup): RecordingsViewHolder? {
      return RecordingsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false))
    }
  }
}