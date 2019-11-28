package com.szr.android.profile.editmyprofile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
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

    /**
     * Observer used for monitoring events send by the view model.
     */
    private val actionObserver: Observer<EditMyProfileViewModel.Action> = Observer { action ->
        when (action) {
            EditMyProfileViewModel.Action.SAVED -> findNavController().popBackStack()
            EditMyProfileViewModel.Action.ERROR_AGE_TOO_LOW -> {
                showError(R.string.snackbar_age_too_low)
            }
            EditMyProfileViewModel.Action.ERROR_SAVING -> {
                showError(R.string.snackbar_error_saving_info)
            }
            null -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders
            .of(this, viewModelFactory)[EditMyProfileViewModel::class.java]
            .apply {
                action.observe(this@EditMyProfileFragment, actionObserver)
            }
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

    private fun showError(errorRes: Int) {
        Snackbar.make(requireView(), errorRes, Snackbar.LENGTH_LONG).show()
    }
}
