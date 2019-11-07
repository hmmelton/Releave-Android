package com.szr.android.views.tabscontent

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.szr.android.nearbyusers.NearbyUsersFragment
import com.szr.android.profile.MyProfileFragment

class TabsContentPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // TODO: add messages fragment later
    private val fragments = listOf(
        MyProfileFragment(),
        NearbyUsersFragment()
    )

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = 2 // TODO: change to 3 when message tab is added
}