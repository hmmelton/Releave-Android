package com.szr.android.profile.editmyprofile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.szr.android.R
import com.szr.android.databinding.FragmentEditMyProfileBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class EditMyProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: EditMyProfileViewModel.Factory
    private lateinit var viewModel: EditMyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders
            .of(this, viewModelFactory)[EditMyProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentEditMyProfileBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_my_profile,
            container,
            false
        )

        binding.viewModel = viewModel
        return binding.root
    }
}
