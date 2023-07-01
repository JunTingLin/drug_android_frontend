package com.junting.drug_android_frontend.ui.pillBoxManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.junting.drug_android_frontend.DrugRecordActivity
import com.junting.drug_android_frontend.DrugbagInfoActivity
import com.junting.drug_android_frontend.PhotoTakeActivity
import com.junting.drug_android_frontend.PillBoxViewManager
import com.junting.drug_android_frontend.R
import com.junting.drug_android_frontend.databinding.FragmentPillBoxManagementBinding
import com.junting.drug_android_frontend.model.drug_record.DrugRecord
import com.junting.drug_android_frontend.ui.drugRecords.DrugRecordsViewModel

class PillBoxManagementFragment : Fragment() {

    private var _binding: FragmentPillBoxManagementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewManager: PillBoxViewManager
    private val positions = (1..9).toList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val ViewModel =DrugRecordsViewModel()

        _binding = FragmentPillBoxManagementBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewManager = PillBoxViewManager(binding,requireContext()) // Initialize the view manager

        // 隱藏所有藥物位置
        positions.forEach { i -> initCell(i) }

        ViewModel.fetchRecordsByAll()
        ViewModel.records.observe(viewLifecycleOwner, Observer {


            // 遍历记录并更新 UI
            for (record in it) {
                Log.d("PillBoxManagementFragment", "record: $record")
                when (record.position) {
                    1 -> viewManager.showCell(1, record, true)
                    2 -> viewManager.showCell(2, record, true)
                    3 -> viewManager.showCell(3, record, true)
                    4 -> viewManager.showCell(4, record,true)
                    5 -> viewManager.showCell(5, record,true)
                    6 -> viewManager.showCell(6, record,true)
                    7 -> viewManager.showCell(7, record,true)
                    8 -> viewManager.showCell(8, record,true)
                    9 -> viewManager.showCell(9, record,true)
                }
            }
            positions.forEach { i -> viewManager.closeProgressBar(i) }
        })

        val inflater = LayoutInflater.from(context)
        val instructionLayout = inflater.inflate(R.layout.instruction_short_short_long, binding.llInstruction, false)
        binding.llInstruction.addView(instructionLayout)
        
        return root
    }
    fun initCell(drugPositionId: Int) {
        val cellResourceId = viewManager.getResourceIdByPosition(drugPositionId)
        val cellView = binding.root.findViewById<View>(cellResourceId)
        val drugIcon = cellView?.findViewById<ImageView>(R.id.iv_drug_icon)!!
        val chipStock = cellView.findViewById<Chip>(R.id.chip_stock)!!
        val drugName = cellView.findViewById<TextView>(R.id.tv_drug_name)!!
        val progressBar = cellView.findViewById<ProgressBar>(R.id.progressBar)!!
        progressBar.visibility = View.VISIBLE
        drugIcon.visibility = View.GONE
        drugName.visibility = View.GONE
        chipStock.visibility = View.GONE

        val cardView = cellView.findViewById<View>(R.id.card_view)
        cardView?.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
                .setTitle("詢問")
                .setMessage("請問您要使用何種方式輸入藥品?")
                .setPositiveButton("手動") { dialogInterface, i ->
                    ContextCompat.startActivity(
                        requireContext(),
                        Intent(context, DrugbagInfoActivity::class.java),
                        null
                    )
                    dialogInterface.dismiss()
                }
                .setNegativeButton("拍攝") { dialogInterface, i ->
                    ContextCompat.startActivity(
                        requireContext(),
                        Intent(context, PhotoTakeActivity::class.java),
                        null
                    )
                }
            builder.create()
            builder.show()
        }
        cardView?.setOnLongClickListener { view ->
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("提示")
            builder.setMessage("該格藥盒已開啟")
            builder.setPositiveButton("確定") { _, _ ->
                // Handle positive button click
            }
            val alertDialog = builder.create()
            alertDialog.show()
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}