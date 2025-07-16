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

    private val faqData = listOf(
        FAQItem("Làm thế nào để thiết lập thời gian cho ứng dụng?",
            "Bạn có thể thiết lập thời gian bằng cách nhấn nút 'Thiết lập' trong tab Hẹn giờ, sau đó chọn ứng dụng và thời gian mong muốn."),
        FAQItem("Ứng dụng có hoạt động khi tôi không mở nó không?",
            "Có, ứng dụng sẽ tiếp tục đếm ngược thời gian ngay cả khi bạn không mở nó. Tuy nhiên, bạn cần cấp quyền truy cập trong cài đặt trợ năng."),
        FAQItem("Tôi có thể thiết lập nhiều ứng dụng cùng lúc không?",
            "Hiện tại phiên bản miễn phí chỉ hỗ trợ 1 ứng dụng. Để sử dụng nhiều ứng dụng, bạn cần nâng cấp lên Premium."),
        FAQItem("Làm thế nào để bật thông báo?",
            "Vào tab Cá nhân > Cài đặt > bật công tắc Thông báo. Đảm bảo bạn đã cấp quyền thông báo cho ứng dụng trong cài đặt hệ thống."),
        FAQItem("Tại sao ứng dụng không hoạt động?",
            "Hãy kiểm tra xem bạn đã cấp quyền truy cập trong Cài đặt > Trợ năng chưa. Đây là quyền bắt buộc để ứng dụng có thể giám sát thời gian sử dụng."),
        FAQItem("Dữ liệu của tôi có được lưu trữ ở đâu?",
            "Tất cả dữ liệu được lưu trữ cục bộ trên thiết bị của bạn. Chúng tôi không thu thập hay lưu trữ dữ liệu cá nhân trên server."),
        FAQItem("Làm thế nào để khôi phục cài đặt mặc định?",
            "Bạn có thể gỡ cài đặt và cài đặt lại ứng dụng, hoặc liên hệ với chúng tôi qua tab Góp ý để được hỗ trợ."),
        FAQItem("Premium có những tính năng gì?",
            "Premium bao gồm: không giới hạn số ứng dụng, thống kê chi tiết, không quảng cáo, và nhiều tính năng cao cấp khác.")
    )

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
            cardElevation = 12f
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

        headerLayout.addView(questionNumber)
        headerLayout.addView(questionText)
        headerLayout.addView(expandIcon)

        mainLayout.addView(headerLayout)
        mainLayout.addView(answerText)

        cardView.addView(mainLayout)

        // Click listener for expand/collapse
        cardView.setOnClickListener {
            if (answerText.visibility == View.VISIBLE) {
                answerText.visibility = View.GONE
                expandIcon.animate().rotation(0f).setDuration(200).start()
            } else {
                answerText.visibility = View.VISIBLE
                expandIcon.animate().rotation(180f).setDuration(200).start()
            }
        }

        return cardView
    }

    data class FAQItem(val question: String, val answer: String)
}
