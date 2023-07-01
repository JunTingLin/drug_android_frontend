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
import com.junting.drug_android_frontend.DrugRecordActivity
import com.junting.drug_android_frontend.R
import com.junting.drug_android_frontend.TakeRecordActivity
import com.junting.drug_android_frontend.model.take_record.DateTakeRecordsRecord
import com.junting.drug_android_frontend.model.take_record.TakeRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TakeRecordsByDrugRecyclerViewAdapter(
    private val context: Context,
    private val dateTakeRecordsRecord: DateTakeRecordsRecord) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DATE = 0
        private const val VIEW_TYPE_TAKE_RECORD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_DATE
        } else {
            VIEW_TYPE_TAKE_RECORD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
                DateViewHolder(view)
            }
            VIEW_TYPE_TAKE_RECORD -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.take_records_by_drug_item_view, parent, false)
                TakeRecordViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateViewHolder -> {
                holder.bind(dateTakeRecordsRecord.date)
            }
            is TakeRecordViewHolder -> {
                val takeRecord = dateTakeRecordsRecord.takeRecords[position - 1]
                holder.bind(takeRecord)
            }
            else -> throw IllegalArgumentException("Invalid view holder type")
        }
    }

    override fun getItemCount(): Int {
        // Add 1 for the date item
        return dateTakeRecordsRecord.takeRecords.size + 1
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(date: String) {
            val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

            val parsedDate = inputFormat.parse(date)

            val dayOfWeek = Calendar.getInstance().apply {
                time = parsedDate
            }.get(Calendar.DAY_OF_WEEK)

            val dayOfWeekString = when (dayOfWeek) {
                Calendar.MONDAY -> itemView.context.getString(R.string.monday)
                Calendar.TUESDAY -> itemView.context.getString(R.string.tuesday)
                Calendar.WEDNESDAY -> itemView.context.getString(R.string.wednesday)
                Calendar.THURSDAY -> itemView.context.getString(R.string.thursday)
                Calendar.FRIDAY -> itemView.context.getString(R.string.friday)
                Calendar.SATURDAY -> itemView.context.getString(R.string.saturday)
                Calendar.SUNDAY -> itemView.context.getString(R.string.sunday)
                else -> ""
            }

            val finalText = "$dayOfWeekString $date"
            dateTextView.text = "        "+finalText   //空白字符填充
        }
    }

    inner class TakeRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dosageTextView: TextView = itemView.findViewById(R.id.tv_dosage)
        private val timeSlotTextView: TextView = itemView.findViewById(R.id.tv_time_slot)
        private val statusImageView: ImageView = itemView.findViewById(R.id.iv_status)
        private val takeRecordByDrugItemLinearLayout: ViewGroup = itemView.findViewById(R.id.ll_take_records_by_drug_item)

        fun bind(takeRecord: TakeRecord) {
            // 根據需要設置藥物記錄項目的內容
            dosageTextView.text = takeRecord.dosage.toString()+"單位"
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

