package com.szr.android.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.szr.android.R
import com.szr.android.adapters.TabsContentPagerAdapter
import kotlinx.android.synthetic.main.fragment_tabs_content.*

class TabsContentFragment : Fragment() {

    private lateinit var adapter: TabsContentPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tabs_content , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TabsContentPagerAdapter(fm = childFragmentManager)
        pager.adapter = adapter
        pager.setCurrentItem(1, false)
    }
}