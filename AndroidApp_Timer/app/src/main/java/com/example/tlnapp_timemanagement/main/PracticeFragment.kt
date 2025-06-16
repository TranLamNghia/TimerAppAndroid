package com.example.tlnapp_timemanagement.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tlnapp_timemanagement.R

class PracticeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate a simple placeholder layout for now
        return inflater.inflate(R.layout.fragment_practice_placeholder, container, false)
    }
}
