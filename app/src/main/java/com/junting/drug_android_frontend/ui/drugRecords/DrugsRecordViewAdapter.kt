package com.junting.drug_android_frontend.ui.drugRecords

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.junting.drug_android_frontend.R
import com.junting.drug_android_frontend.databinding.DrugItemViewBinding
import com.junting.drug_android_frontend.model.DrugRecord

class DrugsRecordViewAdapter(private val context: Context, private val viewModel: DrugRecordsViewModel) :
    RecyclerView.Adapter<DrugsRecordViewAdapter.MyViewHolder>() {

    class MyViewHolder(val drugItemViewBinding: DrugItemViewBinding) :
        RecyclerView.ViewHolder(drugItemViewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val drugItemViewBinding =
            DrugItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(drugItemViewBinding)
    }

    override fun getItemCount(): Int {
        return viewModel.records.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drugRecord: DrugRecord = viewModel.records.value!!.get(position)
        holder.drugItemViewBinding.tvDrugName.text = drugRecord.drug.name
        holder.drugItemViewBinding.tvIndication.text = drugRecord.drug.indications
        holder.drugItemViewBinding.tvFrequencyDosage.text = drugRecord.frequency.toString() + " times a day, " + drugRecord.dosage.toString() + " pills each time"
        if (drugRecord.timeSlot.isEmpty()){
            holder.drugItemViewBinding.tvTimeSlot.visibility = View.GONE
        }else{
            var timeSlotLine = ""
            for(timeSlot in drugRecord.timeSlot) {
                timeSlotLine += timeSlot + " "
            }
            holder.drugItemViewBinding.tvTimeSlot.text = timeSlotLine
        }
        holder.drugItemViewBinding.chipStock.text = "庫存: "+drugRecord.stock.toString()
        holder.drugItemViewBinding.tvHospitalDepartment.text = drugRecord.hospitalName.toString() + ", " + drugRecord.hospitalDepartment.toString()

        if (drugRecord.stock > 0) {
            holder.drugItemViewBinding.chipStock.setChipBackgroundColorResource(R.color.md_theme_light_secondaryContainer)
        } else {
            holder.drugItemViewBinding.chipStock.setChipBackgroundColorResource(R.color.md_theme_dark_error)
        }

        if( drugRecord.notificationSetting.status == true ) {
            holder.drugItemViewBinding.ivNotification.setImageResource(R.drawable.ic_outline_notifications_24)
        }else{
            holder.drugItemViewBinding.ivNotification.setImageResource(R.drawable.ic_outline_notifications_off_24)
        }


        holder.drugItemViewBinding.cardView.setOnClickListener {
            Toast.makeText(context, String.format("You clicked %s", drugRecord.drug.name), Toast.LENGTH_SHORT).show()
        }
    }

}