package com.junting.drug_android_frontend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class StatusSpinnerAdapter(context: Context, private val options: Array<String>) :
    ArrayAdapter<String>(context, R.layout.status_item_spinner, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.status_item_spinner, parent, false)

        val iconImageView = view.findViewById<ImageView>(R.id.iv_status_icon)
        val labelTextView = view.findViewById<TextView>(R.id.tv_status_label)

        // 根據選項設定相對應的圖標和文字
        when (options[position]) {
            "未知" -> {
                iconImageView.setImageResource(R.drawable.ic_baseline_question_mark_24)
                labelTextView.text = "未知"
            }
            "已服用" -> {
                iconImageView.setImageResource(R.drawable.ic_baseline_check_circle_24)
                labelTextView.text = "已服用"
            }
            "未服用" -> {
                iconImageView.setImageResource(R.drawable.ic_baseline_cancel_24)
                labelTextView.text = "未服用"
            }
        }

        return view
    }
}
