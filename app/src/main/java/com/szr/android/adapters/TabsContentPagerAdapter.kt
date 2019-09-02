package com.szr.android.adapters

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.szr.android.views.fragments.NearbyUsersFragment
import com.szr.android.views.fragments.ProfileFragment

class TabsContentPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // TODO: add messages fragment later
    private val fragments = listOf(ProfileFragment(), NearbyUsersFragment())

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = 2 // TODO: change to 3 when message tab is added
}