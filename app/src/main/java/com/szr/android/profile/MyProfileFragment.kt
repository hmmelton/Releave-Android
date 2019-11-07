package com.szr.android.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.szr.android.R
import com.szr.android.databinding.FragmentMyProfileBinding

/**
 * This fragment displays the user's profile, and actions such as editing, settings, and logging
 * out.
 */
class MyProfileFragment : Fragment() {

    private val viewModel by viewModels<MyProfileViewModel>()

    private val shouldSignOutObserver: Observer<Boolean> = Observer { shouldSignOut ->
        if (shouldSignOut) findNavController().popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMyProfileBinding = DataBindingUtil.inflate<FragmentMyProfileBinding>(
            inflater,
            R.layout.fragment_my_profile,
            container,
            false
        )

        viewModel.shouldSignOut.observe(this, shouldSignOutObserver)

        binding.viewModel = viewModel

        return binding.root
    }
}
