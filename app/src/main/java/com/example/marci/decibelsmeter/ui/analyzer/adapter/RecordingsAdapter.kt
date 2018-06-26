package com.example.marci.decibelsmeter.ui.analyzer.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.marci.decibelsmeter.player_manager.PlayerManager
import com.example.marci.decibelsmeter.ui.analyzer.viewmodel.RecordViewModel

/**
 ** Created by marci on 2018-06-22.
 */
class RecordingsAdapter(
    val recordingsList: List<RecordViewModel>,
    val playerManager: PlayerManager
) : RecyclerView.Adapter<RecordingsViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecordingsViewHolder.create(parent)

  override fun onBindViewHolder(holder: RecordingsViewHolder, position: Int) {
    holder.bind(recordingsList[position], playerManager)
  }

  override fun getItemCount() = recordingsList.size
}