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
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_analyzer.*
import java.io.File

/**
 ** Created by marci on 2018-06-22.
 */
class AnalyzerFragment : Fragment() {

  private var playerManager = PlayerManager()
  private lateinit var recordingsAdapter: RecordingsAdapter
  private lateinit var prefsManager: PrefsManager
  private val disposables = CompositeDisposable()
  private val recordingsDecibelsList = mutableListOf<Pair<String, Int>>()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.fragment_analyzer, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recyclerView.layoutManager = LinearLayoutManager(context)
  }

  private fun playFromName(fileName: String) {
    val pathToFile = "${context.externalCacheDir.absolutePath}/$fileName"
    playerManager.onPlay(pathToFile)
  }

  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    if (isVisibleToUser) {
      val recordingsList = mutableListOf<RecordViewModel>()
      getRecordingsList(recordingsList)
      if (recordingsList.isEmpty()) {
        showEmptyRecordingsView()
      } else {
        hidEmptyRecordingsView()
        showRecordings(recordingsList)
      }
    }
  }

  private fun getRecordingsList(recordingsList: MutableList<RecordViewModel>) {
    prefsManager = PrefsManager(activity.application)
    prefsManager.getAllFiles()?.forEach {
      val recordName = getFileNameWithoutFormatExtensions(it, "xml")
      saveRecordingsDecibelsToSharedPrefsByName(recordName)
      addRecordingsToList(recordingsList, recordName)
    }
  }

  private fun getFileNameWithoutFormatExtensions(fileName: String, fileExtension: String) =
      fileName.replace(".$fileExtension", "")

  private fun saveRecordingsDecibelsToSharedPrefsByName(recordName: String) {
    prefsManager.createPreferences(recordName).getAll().forEach {
      recordingsDecibelsList.add(Pair(it.key, it.value.toString().toInt()))
    }
  }

  private fun addRecordingsToList(recordingsList: MutableList<RecordViewModel>, recordName: String) {
    recordingsList.add(RecordViewModel(
        mainRecordName = recordName,
        leftSpeakerDecibels = getDecibelsBySpeakerShortcut(
            recordName,
            getString(R.string.left_speaker_shortcut)
        ),
        rightSpeakerDecibels = getDecibelsBySpeakerShortcut(
            recordName,
            getString(R.string.right_speaker_shortcut)
        )
    ))
  }

  private fun getDecibelsBySpeakerShortcut(recordName: String, speakerShortcut: String) =
      recordingsDecibelsList.firstOrNull { it.first.replace(recordName, "") == speakerShortcut }?.second

  private fun showEmptyRecordingsView() {
    noRecordingsLayout.visibility = View.VISIBLE
  }

  private fun hidEmptyRecordingsView() {
    noRecordingsLayout.visibility = View.INVISIBLE
  }

  private fun showRecordings(recordingsList: MutableList<RecordViewModel>) {
    recordingsAdapter = RecordingsAdapter(recordingsList)
    handlePlayButton(recordingsAdapter.publishLeftPlayBtn())
    handlePlayButton(recordingsAdapter.publishRightPlayBtn())
    handleRecRemoveButton(recordingsAdapter.publishLeftRecRemoveBtn(), context.getString(R.string.left_speaker_shortcut))
    handleRecRemoveButton(recordingsAdapter.publishRightRecRemoveBtn(), context.getString(R.string.right_speaker_shortcut))
    recyclerView.adapter = recordingsAdapter
  }

  private fun handlePlayButton(publishPlayButton: Observable<String>) {
    val disposable = publishPlayButton
        .subscribe { fileName ->
          playFromName(fileName)
        }
    disposables.add(disposable)
  }

  private fun handleRecRemoveButton(publishRecRemoveBtn: Observable<RecordViewModel>, speakerShortcut: String) {
    val disposable = publishRecRemoveBtn.subscribe { record ->
      deleteFile("${context.externalCacheDir.absolutePath}/${record.mainRecordName}$speakerShortcut.3gp")
      prefsManager.createPreferences(record.mainRecordName).put(record.mainRecordName + speakerShortcut, null)
      if (currentSharedPrefFileIsEmpty(record)) {
        deleteFile("${context.filesDir.parent}/shared_prefs/${record.mainRecordName}.xml")
        if (prefsManager.getAllFiles()?.isEmpty() == true) {
          noRecordingsLayout.visibility = View.VISIBLE
        }
      }
    }
    disposables.add(disposable)
  }

  private fun deleteFile(fileName: String) {
    File(fileName).delete()
  }

  private fun currentSharedPrefFileIsEmpty(record: RecordViewModel) =
      prefsManager.createPreferences(record.mainRecordName).getAll().isEmpty()

  override fun onStop() {
    super.onStop()
    playerManager.destroy()
    disposables.dispose()
  }
}