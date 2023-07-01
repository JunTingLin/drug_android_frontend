package com.junting.drug_android_frontend.ui.takeRecords

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.junting.drug_android_frontend.R
import com.junting.drug_android_frontend.TakeRecordActivity
import com.junting.drug_android_frontend.model.take_record.TakeRecord

class TakeRecordsByTimeRecyclerViewAdapter(
    private val context: Context,
    private val takeRecords: List<TakeRecord>
) : RecyclerView.Adapter<TakeRecordsByTimeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.take_records_by_time_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val takeRecord = takeRecords[position]
        holder.bind(takeRecord)
    }

    override fun getItemCount(): Int {
        return takeRecords.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val drugNameTextView: TextView = itemView.findViewById(R.id.tv_drug_name)
        private val dosageTextView: TextView = itemView.findViewById(R.id.tv_dosage)
        private val timeSlotTextView: TextView = itemView.findViewById(R.id.tv_time_slot)
        private val statusImageView: ImageView = itemView.findViewById(R.id.iv_status)
        private val takeRecordByDrugItemLinearLayout: ViewGroup = itemView.findViewById(R.id.ll_take_records_by_time_item)

        fun bind(takeRecord: TakeRecord) {
            drugNameTextView.text = takeRecord.drug.name
            dosageTextView.text = takeRecord.dosage.toString()
            timeSlotTextView.text = takeRecord.timeSlot
            if (takeRecord.status == 0) {
                statusImageView.setImageResource(R.drawable.ic_baseline_question_mark_24)
            } else if (takeRecord.status == 1) {
                timeSlotTextView.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_primary))
                statusImageView.setImageResource(R.drawable.ic_baseline_check_circle_24)
            }else{
                timeSlotTextView.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_error))
                statusImageView.setImageResource(R.drawable.ic_baseline_cancel_24)
            }
            takeRecordByDrugItemLinearLayout.setOnClickListener {
                val intent = Intent(context, TakeRecordActivity::class.java)
                intent.putExtra("takeRecordId", takeRecord.id)
                context.startActivity(intent)
            }
        }
    }
}

