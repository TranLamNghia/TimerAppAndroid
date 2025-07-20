package com.example.tlnapp_timemanagement.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.tlnapp_timemanagement.R

class FAQFrag_ProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        val faqContainer = view.findViewById<LinearLayout>(R.id.faq_container)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupFAQItems(faqContainer)
    }

    private fun setupFAQItems(container: LinearLayout) {

        val questions = resources.getStringArray(R.array.faq_questions)
        val answers = resources.getStringArray(R.array.faq_answers)
        val imageNames = resources.getStringArray(R.array.faq_images)

        val faqData = questions.indices.map { idx ->
            val imageResId = if (imageNames[idx].isNullOrBlank()) null
            else resources.getIdentifier(imageNames[idx], "drawable", requireContext().packageName)
            FAQItem(
                question = questions[idx],
                answer = answers[idx],
                imageResId = imageResId
            )
        }

        faqData.forEachIndexed { index, faqItem ->
            val faqView = createFAQItemView(faqItem, index)
            container.addView(faqView)
        }
    }

    private fun createFAQItemView(faqItem: FAQItem, index: Int): View {
        val cardView = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 32)
            }
            radius = 48f
            cardElevation = 5f
            setCardBackgroundColor(resources.getColor(android.R.color.white))
        }

        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
        }

        val headerLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val questionNumber = TextView(requireContext()).apply {
            text = "${index + 1}"
            textSize = 16f
            setTextColor(resources.getColor(R.color.colorPrimary))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            background = resources.getDrawable(R.drawable.question_number_background)
            setPadding(24, 12, 24, 12)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 32, 0)
            }
        }

        val questionText = TextView(requireContext()).apply {
            text = faqItem.question
            textSize = 18f
            setTextColor(resources.getColor(R.color.textPrimary))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val expandIcon = ImageView(requireContext()).apply {
            setImageResource(R.drawable.ic_arrow_down)
            setColorFilter(resources.getColor(R.color.textSecondary))
            layoutParams = LinearLayout.LayoutParams(48, 48)
        }

        val answerText = TextView(requireContext()).apply {
            text = faqItem.answer
            textSize = 16f
            setTextColor(resources.getColor(R.color.textSecondary))
            setPadding(0, 32, 0, 0)
            visibility = View.GONE
            setLineSpacing(8f, 1f)
        }

        var answerImage: ImageView? = null
        faqItem.imageResId?.let { imgResId ->
            if (imgResId != 0) {
                answerImage = ImageView(requireContext()).apply {
                    setImageResource(imgResId)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    adjustViewBounds = true
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 200
                    ).apply {
                        setMargins(0, 24, 0, 0)
                    }
                    visibility = View.GONE // Ẩn cùng với answer ban đầu
                }
            }
        }

        headerLayout.addView(questionNumber)
        headerLayout.addView(questionText)
        headerLayout.addView(expandIcon)

        mainLayout.addView(headerLayout)
        mainLayout.addView(answerText)
        answerImage?.let { mainLayout.addView(it) }

        cardView.addView(mainLayout)

        cardView.setOnClickListener {
            val show = answerText.visibility != View.VISIBLE
            answerText.visibility = if (show) View.VISIBLE else View.GONE
            answerImage?.visibility = if (show) View.VISIBLE else View.GONE
            expandIcon.animate().rotation(0f).setDuration(200).start()
        }

        return cardView
    }

    data class FAQItem(
        val question: String,
        val answer: String,
        val imageResId: Int?
    )
}
