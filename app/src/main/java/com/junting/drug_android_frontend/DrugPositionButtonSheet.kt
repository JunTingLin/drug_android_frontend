package com.junting.drug_android_frontend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.junting.drug_android_frontend.databinding.FragmentPillBoxManagementBinding
import com.junting.drug_android_frontend.services.BTServices.BluetoothSocket
import com.junting.drug_android_frontend.ui.drugRecords.DrugRecordsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrugPositionButtonSheet(viewModel: DrugRecordsViewModel) : BottomSheetDialogFragment() {

    private var _binding: FragmentPillBoxManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DrugRecordsViewModel
    private val positions = (1..9).toList()
    private lateinit var pillBoxViewManager: PillBoxViewManager

    init {
        this.viewModel = viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPillBoxManagementBinding.inflate(inflater, container, false)
        pillBoxViewManager = PillBoxViewManager(binding,requireContext()) // Initialize the view manager

        positions.forEach { i -> initCell(i) }
        viewModel.record.observe(viewLifecycleOwner) {
            if (positions.contains(it.position)) {
                pillBoxViewManager.showCell(it.position, it,false)
                pillBoxViewManager.setCellColor(it.position)
                pillBoxViewManager.closeProgressBar(it.position)
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
                .forEach { record -> pillBoxViewManager.showCell(record.position, record,false) }
            positions.forEach { i -> pillBoxViewManager.closeProgressBar(i) }
        }

        val inflater = LayoutInflater.from(context)
        val instructionLayout = inflater.inflate(R.layout.instruction_short_long, binding.llInstruction, false)
        binding.llInstruction.addView(instructionLayout)

        return binding.root
    }

    private fun initCell(position: Int) {
        val drugPositionId = pillBoxViewManager.getResourceIdByPosition(position)
        val cellView = binding.root.findViewById<View>(drugPositionId)
        val cardView = cellView.findViewById<View>(R.id.card_view)
        pillBoxViewManager.hideCell(position,positions)
        cardView?.setOnClickListener {
            if (!isPositionEmpty(position)) {
                return@setOnClickListener
            }
            val selectedDrugPositionIdNumber =
                resources.getResourceEntryName(cellView.id).substringAfterLast("_")
            Log.d("CellClicked", "Cell ID: $selectedDrugPositionIdNumber")
            pillBoxViewManager.resetCellsColor(positions)
            pillBoxViewManager.setCellColor(selectedDrugPositionIdNumber.toInt())
            val oldPosition = viewModel.record.value!!.position
            pillBoxViewManager.hideCell(oldPosition,positions)
            updateDrugRecordsPosition(oldPosition, position)
            viewModel.setPosition(position)
        }
        cardView?.setOnLongClickListener { view ->
            val builder = MaterialAlertDialogBuilder(requireContext())

            var bs = BluetoothSocket()
            lifecycleScope.launch(Dispatchers.IO) {
                bs.openPillbox(position.toString())
            }

            builder.setTitle(resources.getString(R.string.hint_title))
            builder.setMessage(resources.getString(R.string.pillbox_management_hint_message))
            builder.setPositiveButton(resources.getString(R.string.close_pillbox)) { _, _ ->
                Log.d("Bosh here", "close pillbox position: ${position}")
                // Handle positive button click
                lifecycleScope.launch(Dispatchers.IO) {
                    bs.closePillbox(position.toString())
                }
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
