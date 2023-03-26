package com.junting.drug_android_frontend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.junting.drug_android_frontend.model.InteractingDrugs
import com.junting.drug_android_frontend.databinding.DrugsInteractionViewBinding

class DrugsInteractionAdapter :
    RecyclerView.Adapter<DrugsInteractionAdapter.MyViewHolder>() {

    //List是要取得的資料型態
    var DrugsInteractionList: List<InteractionDrugs> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val drugsInteractionViewBinding =
            DrugsInteractionViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(drugsInteractionViewBinding)
    }

    override fun getItemCount(): Int {
        return DrugsInteractionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.drugsInteractionViewBinding.dilDrugNameBtn.text =
            DrugsInteractionList[position].drug.name.toString()
        holder.drugsInteractionViewBinding.dilInteractionCause.text =
            DrugsInteractionList[position].cause.toString()
        holder.drugsInteractionViewBinding.dilInteractionLevel.text =
            DrugsInteractionList[position].interactionDrugs.level.toString()
    }

    class MyViewHolder(val drugsInteractionViewBinding: DrugsInteractionViewBinding) :
        RecyclerView.ViewHolder(drugsInteractionViewBinding.root)


}