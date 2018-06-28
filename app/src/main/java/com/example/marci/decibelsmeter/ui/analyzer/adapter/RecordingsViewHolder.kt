package com.example.marci.decibelsmeter.ui.analyzer.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marci.decibelsmeter.R
import com.example.marci.decibelsmeter.ui.analyzer.viewmodel.RecordViewModel
import kotlinx.android.synthetic.main.item_record.view.*

/**
 ** Created by marci on 2018-06-22.
 */
class RecordingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  fun bind(record: RecordViewModel) {
    with(itemView) {
      recordMainNameTextView.text = record.mainRecordName
      leftSpeakerDecibelsTextView.text = context.getString(
          R.string.decibels,
          record.leftSpeakerDecibels ?: 0
      )
      rightSpeakerDecibelsTextView.text = context.getString(
          R.string.decibels,
          record.rightSpeakerDecibels ?: 0
      )
      if (record.leftSpeakerDecibels == null) {
        leftPlayButton.visibility = View.INVISIBLE
        leftRemoveButton.visibility = View.INVISIBLE
      }
      if (record.rightSpeakerDecibels == null) {
        rightPlayButton.visibility = View.INVISIBLE
        rightRemoveButton.visibility = View.INVISIBLE
      }
      setUpSpeakersStatementTextView.text = record.getStatement()
      speakersStateImageView.setImageDrawable(ContextCompat.getDrawable(context, record.imageView))
    }
  }

  companion object {
    fun create(parent: ViewGroup): RecordingsViewHolder? {
      return RecordingsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false))
    }
  }
}