package com.example.marci.decibelsmeter.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import com.example.marci.decibelsmeter.R
import com.example.marci.decibelsmeter.ui.recording.RecordingFragment
import com.example.marci.decibelsmeter.ui.analyzer.AnalyzerFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 ** Created by marci on 2018-06-22.
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    viewPager.adapter = PageAdapter(supportFragmentManager)
    tabs.setupWithViewPager(viewPager)
  }

  inner class PageAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private val tabTitles = arrayOf(getString(R.string.recording), getString(R.string.player))
    private val pageCount = tabTitles.size

    override fun getItem(position: Int): Fragment {
      when (position) {
        0 -> return RecordingFragment()
        1 -> return AnalyzerFragment()
      }
      return RecordingFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
      return tabTitles[position]
    }

    override fun getCount() = pageCount
  }
}