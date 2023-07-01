package com.junting.drug_android_frontend

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.junting.drug_android_frontend.databinding.FragmentPillBoxManagementBinding
import com.junting.drug_android_frontend.model.drug_record.DrugRecord

class PillBoxViewManager(private val binding: FragmentPillBoxManagementBinding, private val context: Context) {

    fun showCell(drugPositionId: Int, record: DrugRecord, onClickListener: Boolean) {
        val cellResourceId = getResourceIdByPosition(drugPositionId)
        val cellView = binding.root.findViewById<View>(cellResourceId)
        val drugIcon = cellView?.findViewById<ImageView>(R.id.iv_drug_icon)!!
        val chipStock = cellView.findViewById<Chip>(R.id.chip_stock)!!
        val drugName = cellView.findViewById<TextView>(R.id.tv_drug_name)!!
        drugIcon.visibility = View.VISIBLE
        drugName.visibility = View.VISIBLE
        chipStock.visibility = View.VISIBLE
        drugName.text = record.drug.name
        chipStock.text = "庫存: " + record.stock.toString()
        if (record.stock > record.returnSetting.left) {
            chipStock.setChipBackgroundColorResource(R.color.md_theme_light_secondaryContainer)
        } else {
            chipStock.setChipBackgroundColorResource(R.color.md_theme_dark_error)
        }
        if(onClickListener) {
            cellView?.findViewById<View>(R.id.card_view)?.setOnClickListener {
                val intent = Intent(context, DrugRecordActivity::class.java)
                intent.putExtra("drugRecordId", record.id)
                context?.startActivity(intent)
            }
        }

    }

    fun closeProgressBar(drugPositionId: Int) {
        val cellResourceId = getResourceIdByPosition(drugPositionId)
        val cellView = binding.root.findViewById<View>(cellResourceId)
        cellView?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
    }


    fun getResourceIdByPosition(position: Int): Int {
        return context.resources.getIdentifier(
            "ll_drug_position_$position",
            "id",
            context.packageName
        )
    }

    fun hideCell(position: Int, positions: List<Int>) {
        if (positions.contains(position)) {
            val drugPositionId = getResourceIdByPosition(position)
            val cellView = binding.root.findViewById<View>(drugPositionId)
            val drugIcon = cellView?.findViewById<ImageView>(R.id.iv_drug_icon)!!
            val chipStock = cellView.findViewById<Chip>(R.id.chip_stock)!!
            val drugName = cellView.findViewById<TextView>(R.id.tv_drug_name)!!
            val progressBar = cellView.findViewById<ProgressBar>(R.id.progressBar)!!
            progressBar.visibility = View.VISIBLE
            drugIcon.visibility = View.GONE
            drugName.visibility = View.GONE
            chipStock.visibility = View.GONE
        }
    }
    fun resetCellsColor(positions: List<Int>) {
        for (i in positions) {
            val cellResourceId = getResourceIdByPosition(i)
            val cellView = binding.root.findViewById<View>(cellResourceId)
            val cardView = cellView?.findViewById<CardView>(R.id.card_view)
            cardView?.setCardBackgroundColor(context.resources.getColor(android.R.color.background_light))
        }
    }
    fun setCellColor(position: Int) {
        val cellResourceId = getResourceIdByPosition(position)
        val cellView = binding.root.findViewById<View>(cellResourceId)
        val cardView = cellView?.findViewById<CardView>(R.id.card_view)
        cardView?.setCardBackgroundColor(context.resources.getColor(R.color.md_theme_light_secondaryContainer))
    }

}
