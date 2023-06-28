package com.junting.drug_android_frontend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.junting.drug_android_frontend.databinding.FragmentPillBoxManagementBinding
import com.junting.drug_android_frontend.model.drug_record.DrugRecord
import com.junting.drug_android_frontend.ui.drugRecords.DrugRecordsViewModel
import com.junting.drug_android_frontend.services.BluetoothSocket

class DrugPositionButtonSheet(viewModel: DrugRecordsViewModel) : BottomSheetDialogFragment() {

    private var _binding: FragmentPillBoxManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DrugRecordsViewModel
    private val positions = (1..9).toList()
    private lateinit var viewManager: PillBoxViewManager

    init {
        this.viewModel = viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPillBoxManagementBinding.inflate(inflater, container, false)
        viewManager = PillBoxViewManager(binding,requireContext()) // Initialize the view manager

        positions.forEach { i -> initCell(i) }
        viewModel.record.observe(viewLifecycleOwner) {
            if (positions.contains(it.position)) {
                viewManager.showCell(it.position, it,false)
                viewManager.setCellColor(it.position)
                viewManager.closeProgressBar(it.position)
            }
        }
        if (viewModel.records.value == null) {
            viewModel.fetchRecordsByAll()
        }
        viewModel.records.observe(viewLifecycleOwner) {
            it.toMutableList()
            it.stream()
                // skip current editing record
                .filter { record -> record.position != viewModel.record.value?.position }
                .filter { record -> positions.contains(record.position) }
                .forEach { record -> viewManager.showCell(record.position, record,false) }
            positions.forEach { i -> viewManager.closeProgressBar(i) }
        }
        return binding.root
    }

    private fun initCell(position: Int) {
        val drugPositionId = viewManager.getResourceIdByPosition(position)
        val cellView = binding.root.findViewById<View>(drugPositionId)
        val cardView = cellView.findViewById<View>(R.id.card_view)
        viewManager.hideCell(position,positions)
        cardView?.setOnClickListener {
            if (!isPositionEmpty(position)) {
                return@setOnClickListener
            }
            val selectedDrugPositionIdNumber =
                resources.getResourceEntryName(cellView.id).substringAfterLast("_")
            Log.d("CellClicked", "Cell ID: $selectedDrugPositionIdNumber")
            viewManager.resetCellsColor(positions)
            viewManager.setCellColor(selectedDrugPositionIdNumber.toInt())
            val oldPosition = viewModel.record.value!!.position
            viewManager.hideCell(oldPosition,positions)
            updateDrugRecordsPosition(oldPosition, position)
            viewModel.setPosition(position)
        }
        cardView?.setOnLongClickListener { view ->
            val builder = MaterialAlertDialogBuilder(requireContext())

            // identify which cell is clicked
            val selectedDrugPositionIdNumber =
                resources.getResourceEntryName(cellView.id).substringAfterLast("_")
//            Log.d("CellClicked", "Cell ID: $selectedDrugPositionIdNumber")

            // send bluetooth signals
            val bs = BluetoothSocket()
            bs.openPillbox(selectedDrugPositionIdNumber)

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

    private fun updateDrugRecordsPosition(oldPosition: Int, newPosition: Int) {
        val records = viewModel.records.value!!
        records.stream()
            .filter { it.position == oldPosition }
            .findFirst()
            .map { it.position = newPosition }
        viewModel.setRecords(records)
    }

    private fun isPositionEmpty(position: Int): Boolean {
        return viewModel.records.value!!.stream().noneMatch { it.position == position }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解除 ViewBinding 的綁定
        _binding = null
    }

}
