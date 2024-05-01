package com.example.basekotlin.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.basekotlin.ui.history.HistoryFragment
import com.example.basekotlin.ui.tracker.TrackerFragment

class MainAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 1) {
            HistoryFragment()
        } else {
            TrackerFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}