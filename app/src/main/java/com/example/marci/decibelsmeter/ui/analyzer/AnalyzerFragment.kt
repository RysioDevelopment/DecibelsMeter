package com.example.marci.decibelsmeter.ui.analyzer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marci.decibelsmeter.R
import com.example.marci.decibelsmeter.player_manager.PlayerManager
import com.example.marci.decibelsmeter.shared_prefs.PrefsManager
import com.example.marci.decibelsmeter.ui.analyzer.adapter.RecordingsAdapter
import com.example.marci.decibelsmeter.ui.analyzer.viewmodel.RecordViewModel
import kotlinx.android.synthetic.main.fragment_analyzer.*

/**
 ** Created by marci on 2018-06-22.
 */
class AnalyzerFragment : Fragment() {

  private var playerManager = PlayerManager()
  lateinit var recordingsAdapter: RecordingsAdapter
  lateinit var prefsManager: PrefsManager

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.fragment_analyzer, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recyclerView.layoutManager = LinearLayoutManager(context)
  }

  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    if (isVisibleToUser) {
      val recordingsList = mutableListOf<RecordViewModel>()
      val recordingsDecibels = mutableListOf<Pair<String, Int>>()
      prefsManager = PrefsManager(activity.application)
      prefsManager.getAllFiles()?.forEach {
        val recordName = it.replace(".xml", "")
        prefsManager.createPreferences(recordName).getAll().forEach {
          recordingsDecibels.add(Pair(it.key, it.value.toString().toInt()))
        }
        recordingsList.add(RecordViewModel(
            mainRecordName = recordName,
            leftSpeakerDecibels = recordingsDecibels.firstOrNull { it.first.replace(recordName, "") == getString(R.string.left_speaker_shortcut) }?.second,
            rightSpeakerDecibels = recordingsDecibels.firstOrNull { it.first.replace(recordName, "") == getString(R.string.right_speaker_shortcut) }?.second
        ))
      }
      recordingsAdapter = RecordingsAdapter(recordingsList, playerManager)
      recyclerView.adapter = recordingsAdapter
    }
  }

  override fun onStop() {
    super.onStop()
    playerManager.destroy()
  }
}