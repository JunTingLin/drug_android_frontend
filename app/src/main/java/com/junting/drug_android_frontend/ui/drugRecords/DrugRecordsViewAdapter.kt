package com.junting.drug_android_frontend.ui.drugRecords

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.junting.drug_android_frontend.DrugRecordActivity
import com.junting.drug_android_frontend.R
import com.junting.drug_android_frontend.databinding.DrugItemViewBinding
import com.junting.drug_android_frontend.model.drug_record.DrugRecord

class DrugRecordsViewAdapter(private val context: Context, private val viewModel: DrugRecordsViewModel) :
    RecyclerView.Adapter<DrugRecordsViewAdapter.MyViewHolder>() {

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

        if (drugRecord.indicationTag == "") {
            holder.drugItemViewBinding.tvIndicationTag.visibility = View.GONE
        }else{
            holder.drugItemViewBinding.tvIndicationTag.visibility = View.VISIBLE
            holder.drugItemViewBinding.tvIndicationTag.text = drugRecord.indicationTag
        }

        if(drugRecord.frequency==0){
            holder.drugItemViewBinding.tvFrequencyDosage.text = "按需服用," + drugRecord.dosage.toString() + " 單位/次"
        }else{
            holder.drugItemViewBinding.tvFrequencyDosage.text = drugRecord.frequency.toString() + " 次/天, " + drugRecord.dosage.toString() + " 單位/次"
        }

        if (drugRecord.timeSlots.isEmpty()){
            holder.drugItemViewBinding.tvTimeSlots.visibility = View.GONE
        }else{
            holder.drugItemViewBinding.tvTimeSlots.text = drugRecord.timeSlots.joinToString()
        }
        holder.drugItemViewBinding.chipStock.text = "庫存: "+drugRecord.stock.toString()
        holder.drugItemViewBinding.tvHospitalDepartment.text = drugRecord.hospital.name.toString() + ", " + drugRecord.hospital.department.toString()

        if (drugRecord.stock > drugRecord.returnSetting.left) {
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
            val intent = Intent(context, DrugRecordActivity::class.java)
            intent.putExtra("drugRecordId", drugRecord.id)
            context.startActivity(intent)
        }
    }

}