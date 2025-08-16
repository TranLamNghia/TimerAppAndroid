package com.example.tlnapp_timemanagement.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.tlnapp_timemanagement.R

class Premium_ProfileFrag : DialogFragment() {

    private lateinit var btnUpgrade: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_premium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnUpgrade = view.findViewById(R.id.btn_upgrade)
        btnCancel = view.findViewById(R.id.btn_cancel)

        btnUpgrade.setOnClickListener {
            Toast.makeText(context, "Tính năng Premium đang phát triển", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
