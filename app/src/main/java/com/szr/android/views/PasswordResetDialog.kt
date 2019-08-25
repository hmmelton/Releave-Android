package com.szr.android.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.szr.android.R

class PasswordResetDialog : DialogFragment() {

    var callback: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_reset_password, null)
            val emailInput: EditText = view.findViewById(R.id.reset_password)

            val builder = AlertDialog.Builder(it, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setView(view)
                .setTitle(R.string.action_reset_password)
                .setPositiveButton(R.string.btn_reset) { _, _ ->
                    callback?.invoke(emailInput.text.toString())
                }
                .setNegativeButton(android.R.string.cancel, null)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}