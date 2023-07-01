package com.junting.drug_android_frontend

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.junting.drug_android_frontend.databinding.DrugItemViewBinding
import com.junting.drug_android_frontend.databinding.OnDemandItemViewBinding
import com.junting.drug_android_frontend.model.drug_record.DrugRecord

class OnDemandRecyclerViewAdapter(private val context: Context, private val viewModel: OnDemandViewModel) :
    RecyclerView.Adapter<OnDemandRecyclerViewAdapter.MyViewHolder>() {

    class MyViewHolder(val onDemandItemViewBinding: OnDemandItemViewBinding) :
        RecyclerView.ViewHolder(onDemandItemViewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val drugItemViewBinding =
            OnDemandItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(drugItemViewBinding)
    }

    override fun getItemCount(): Int {
        return viewModel.onDemandDrugRecors.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drugRecord: DrugRecord = viewModel.onDemandDrugRecors.value!!.get(position)
        holder.onDemandItemViewBinding.tvDrugName.text = drugRecord.drug.name
        holder.onDemandItemViewBinding.tvIndicationTag.text = drugRecord.indicationTag

        holder.onDemandItemViewBinding.cardView.setOnClickListener {
            val intent = Intent(context, OnDemandActivity::class.java)
            intent.putExtra("drugRecordId", drugRecord.id)
            context.startActivity(intent)
        }
    }

}