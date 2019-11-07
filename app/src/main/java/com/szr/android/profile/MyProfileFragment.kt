package com.szr.android.profile


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.szr.android.R
import com.szr.android.profile.MyProfileViewModel.ButtonAction
import com.szr.android.databinding.FragmentMyProfileBinding

/**
 * This fragment displays the user's profile, and actions such as editing, settings, and logging
 * out.
 */
private const val TAG = "MyProfileFragment"
class MyProfileFragment : Fragment() {

    private val viewModel by viewModels<MyProfileViewModel>()

    /**
     * Observer needed for responding to button clicks with navigation actions
     */
    private val shouldSignOutObserver: Observer<ButtonAction> =
        Observer { action ->
            when (action) {
                ButtonAction.EDIT_PROFILE -> {}
                ButtonAction.SETTINGS -> {}
                ButtonAction.SIGN_OUT -> findNavController().popBackStack()
                else -> Log.wtf(TAG, "Unrecognized button action: {$action, ${action.name}}")
            }
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

        viewModel.buttonAction.observe(this, shouldSignOutObserver)
        binding.viewModel = viewModel

        return binding.root
    }
}
