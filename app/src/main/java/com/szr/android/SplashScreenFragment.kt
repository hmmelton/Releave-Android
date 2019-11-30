package com.szr.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.szr.android.R

class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // If user is logged in, navigate to main screen, else navigate to login
        val destination = if (auth.currentUser != null && auth.currentUser?.isEmailVerified == true) {
            R.id.action_splashScreenFragment_to_tabsContentFragment
        } else {
            R.id.action_splashScreenFragment_to_signInFragment
        }
        findNavController().navigate(destination)
    }
}
