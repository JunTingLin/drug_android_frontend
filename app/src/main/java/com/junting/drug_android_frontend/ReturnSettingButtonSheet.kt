package com.junting.drug_android_frontend

import DialogUtils
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.junting.drug_android_frontend.databinding.BottomSheetReturnSettingBinding
import com.junting.drug_android_frontend.libs.DateUtils
import com.junting.drug_android_frontend.ui.drugRecords.DrugRecordsViewModel

class ReturnSettingButtonSheet(viewModel: DrugRecordsViewModel) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetReturnSettingBinding? = null
    private val binding get() = _binding!!

    private var viewModel: DrugRecordsViewModel = viewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 使用 ViewBinding 綁定佈局檔案
        _binding = BottomSheetReturnSettingBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setReturnSettingDate(calculateRemainingDate())
        DialogUtils.initTextViewEditDialog(
            requireContext(),
            binding.llLeft,
            binding.tvLeft,
            "修改剩餘庫存提醒",
            true
        ) { text ->
            viewModel.setReturnSettingLeft(text.toInt())
            viewModel.setReturnSettingDate(calculateRemainingDate())
        }

        viewModel.record.observe(viewLifecycleOwner, Observer { record ->
            val left = record.returnSetting?.left
            val date = record.returnSetting?.date
            val instruction = " * 當庫存剩餘$left 時的回診日期為 $date"
            binding.tvInstruction.text = instruction
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解除 ViewBinding 的綁定
        _binding = null
    }
    private fun calculateRemainingDate(): String{
        var remainingDate = DateUtils.calculateRemainingDate(
            viewModel.record.value!!.timeSlots,
            viewModel.record.value!!.dosage,
            viewModel.record.value!!.stock,
            viewModel.record.value!!.returnSetting.left
        )
        return remainingDate
    }
}
