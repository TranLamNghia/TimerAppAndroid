package com.example.tlnapp_timemanagement.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tlnapp_timemanagement.network.AirtableClient
import com.example.tlnapp_timemanagement.R
import com.google.gson.JsonObject

class FeedbackFrag_ProfileFragment : Fragment(){

    private lateinit var backButton: ImageView
    private lateinit var feedbackTypeSpinner: Spinner
    private lateinit var nameEditText: EditText
    private lateinit var subjectEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var ratingBar: RatingBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupSpinner()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        backButton = view.findViewById(R.id.back_button)
        feedbackTypeSpinner = view.findViewById(R.id.feedback_type_spinner)
        nameEditText = view.findViewById(R.id.name_edittext)
        subjectEditText = view.findViewById(R.id.subject_edittext)
        messageEditText = view.findViewById(R.id.message_edittext)
        emailEditText = view.findViewById(R.id.email_edittext)
        submitButton = view.findViewById(R.id.submit_button)
        ratingBar = view.findViewById(R.id.rating_bar)
    }

    private fun setupSpinner() {
        val feedbackTypes = arrayOf(
            "Chọn loại góp ý",
            "Báo lỗi",
            "Đề xuất tính năng",
            "Cải thiện giao diện",
            "Hiệu suất ứng dụng",
            "Khác"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, feedbackTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        feedbackTypeSpinner.adapter = adapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        submitButton.setOnClickListener {
            submitFeedback()
        }
    }

    private fun submitFeedback() {
        val feedbackType = feedbackTypeSpinner.selectedItem.toString()
        val name = nameEditText.text.toString().trim()
        val subject = subjectEditText.text.toString().trim()
        val message = messageEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val rating = ratingBar.rating

        // Validation
        if (feedbackType == "Chọn loại góp ý") {
            Toast.makeText(context, "Vui lòng chọn loại góp ý", Toast.LENGTH_SHORT).show()
            return
        }

        if (subject.isEmpty()) {
            subjectEditText.error = "Vui lòng nhập tiêu đề"
            return
        }

        if (message.isEmpty()) {
            messageEditText.error = "Vui lòng nhập nội dung góp ý"
            return
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Vui lòng nhập email hợp lệ"
            return
        }

        // Simulate feedback submission
        submitButton.isEnabled = false
        submitButton.text = "Đang gửi..."

        // Simulate network delay
        submitButton.postDelayed({
            // Send data
            val fields = JsonObject().apply{
                addProperty("NameUser", name)
                addProperty("Type", feedbackType)
                addProperty("Subject", subject)
                addProperty("Message", message)
                addProperty("Email", email)
                addProperty("Rating", rating)
            }

            val payload = JsonObject().apply {
                add("fields", fields)
            }

            val jsonBody = payload.toString()

            AirtableClient.sendFeedback(jsonBody) { success, errorMsg ->
                // Callback chạy ngầm background thread của OkHttp, phải về UI thread
                requireActivity().runOnUiThread {
                    if (success) {
                        Toast.makeText(context,
                            "Cảm ơn bạn đã gửi góp ý!", Toast.LENGTH_LONG).show()
                        clearForm()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(context,
                            "Gửi thất bại: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                    submitButton.isEnabled = true
                    submitButton.text = "Gửi góp ý"
                }
            }
        }, 2000)
    }

    private fun clearForm() {
        feedbackTypeSpinner.setSelection(0)
        nameEditText.text.clear()
        subjectEditText.text.clear()
        messageEditText.text.clear()
        emailEditText.text.clear()
        ratingBar.rating = 0f
    }
}