package com.releave.android.fragments


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

import com.releave.android.R

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menuSignOut) {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
