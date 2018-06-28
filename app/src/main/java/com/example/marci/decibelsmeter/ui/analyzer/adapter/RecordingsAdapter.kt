package com.example.marci.decibelsmeter.ui.analyzer.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.marci.decibelsmeter.R
import com.example.marci.decibelsmeter.ui.analyzer.viewmodel.RecordViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_record.view.*

/**
 ** Created by marci on 2018-06-22.
 */
class RecordingsAdapter(
    private val recordingsList: MutableList<RecordViewModel>
) : RecyclerView.Adapter<RecordingsViewHolder>() {

  private val leftPlayPublishSubject = PublishSubject.create<String>()
  private val rightPlayPublishSubject = PublishSubject.create<String>()
  private val leftRecRemovePublishSubject = PublishSubject.create<RecordViewModel>()
  private val rightRecRemovePublishSubject = PublishSubject.create<RecordViewModel>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecordingsViewHolder.create(parent)

  override fun onBindViewHolder(holder: RecordingsViewHolder, position: Int) {
    holder.bind(recordingsList[position])
    setUpLeftPlayButton(holder, position)
    setUpRightPlayButton(holder, position)
    setUpLeftRemoveButton(holder, position)
    setUpRightRemoveButton(holder, position)
  }

  private fun setUpRightRemoveButton(holder: RecordingsViewHolder, position: Int) {
    holder.itemView.rightRemoveButton.setOnClickListener {
      rightRecRemovePublishSubject.onNext(recordingsList[position])
      holder.itemView.rightPlayButton.visibility = View.INVISIBLE
      it.visibility = View.INVISIBLE
      if (isDeletedRecords(holder)) {
        recordingsList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
      } else {
        notifyDataSetChanged()
      }
    }
  }

  private fun setUpLeftRemoveButton(holder: RecordingsViewHolder, position: Int) {
    holder.itemView.leftRemoveButton.setOnClickListener {
      leftRecRemovePublishSubject.onNext(recordingsList[position])
      it.visibility = View.INVISIBLE
      holder.itemView.leftPlayButton.visibility = View.INVISIBLE
      if (isDeletedRecords(holder)) {
        recordingsList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
      } else {
        notifyDataSetChanged()
      }
    }
  }

  private fun isDeletedRecords(holder: RecordingsViewHolder) =
      holder.itemView.leftPlayButton.visibility == View.INVISIBLE && holder.itemView.rightPlayButton.visibility == View.INVISIBLE

  private fun setUpRightPlayButton(holder: RecordingsViewHolder, position: Int) {
    holder.itemView.rightPlayButton.setOnClickListener {
      rightPlayPublishSubject.onNext(recordingsList[position].mainRecordName + "${it.context.getString(R.string.right_speaker_shortcut)}.3gp")
      it.isSelected = !it.isSelected
    }
  }

  private fun setUpLeftPlayButton(holder: RecordingsViewHolder, position: Int) {
    holder.itemView.leftPlayButton.setOnClickListener {
      leftPlayPublishSubject.onNext(recordingsList[position].mainRecordName + "${it.context.getString(R.string.left_speaker_shortcut)}.3gp")
      it.isSelected = !it.isSelected
    }
  }

  override fun getItemCount() = recordingsList.size

  fun publishLeftPlayBtn(): Observable<String> = leftPlayPublishSubject

  fun publishRightPlayBtn(): Observable<String> = rightPlayPublishSubject

  fun publishLeftRecRemoveBtn(): Observable<RecordViewModel> = leftRecRemovePublishSubject

  fun publishRightRecRemoveBtn(): Observable<RecordViewModel> = rightRecRemovePublishSubject
}