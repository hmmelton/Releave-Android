package com.szr.android.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.szr.android.R

class SplashScreenFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // If user is logged in, navigate to main screen, else navigate to login
        if (auth.currentUser != null && auth.currentUser?.isEmailVerified == true) {
            findNavController().navigate(R.id.action_splashScreenFragment_to_nearbyUsersFragment)
        } else {
            findNavController().navigate(R.id.action_splashScreenFragment_to_signInFragment)
        }
    }
}
