package com.example.tlnapp_timemanagement.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.example.tlnapp_timemanagement.R

class LoadingFrag : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = android.app.AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.fragment_loading, null)
        val lottieView = view.findViewById<LottieAnimationView>(R.id.lottieLoading)
        lottieView.speed = 5f
        builder.setView(view)
        isCancelable = false
        return builder.create()
    }

}