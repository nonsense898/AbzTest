package com.non.abztest.utils

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.non.abztest.R

class FullScreenDialogFragment(private val layoutResId: Int) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeButton: ImageView? = view.findViewById(R.id.dismissIcon)
        closeButton?.setOnClickListener { dismiss() }

        view.findViewById<Button>(R.id.gotItBtn)?.setOnClickListener { onButtonClicked(it.id) }
        view.findViewById<Button>(R.id.tryAgainFailedBtn)?.setOnClickListener { onButtonClicked(it.id) }
    }

    private fun onButtonClicked(viewId: Int) {
        when (viewId) {
            R.id.gotItBtn -> {
                dismiss()
            }
            R.id.tryAgainFailedBtn -> {
                dismiss()
            }
            else -> {
            }
        }
    }


}