package com.junting.drug_android_frontend

import DialogUtils
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.junting.drug_android_frontend.databinding.BottomSheetNotificationSettingBinding
import com.junting.drug_android_frontend.ui.drugRecords.DrugRecordsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationSettingButtonSheet(viewModel: DrugRecordsViewModel) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetNotificationSettingBinding? = null
    private val binding get() = _binding!!

    private var viewModel: DrugRecordsViewModel = viewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 使用 ViewBinding 綁定佈局檔案
        _binding = BottomSheetNotificationSettingBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llStartDate.setOnClickListener{
            showDatePickerDialog()
        }
        DialogUtils.initTextViewEditDialog(
            requireContext(),
            binding.llRepeat,
            binding.tvRepeat,
            "修改重複提醒次數",
            true
        ) { text ->
            viewModel.setNotificationSettingRepeat(text.toInt())

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解除 ViewBinding 的綁定
        _binding = null
    }

    private fun showDatePickerDialog() {
        // 使用 MaterialDatePicker 顯示日期選擇對話框
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder
            .setTitleText("選擇開始服藥日期")
            .setPositiveButtonText("確定")
            .setNegativeButtonText("取消")
            .build()

        // 設置選擇日期後的回調
        picker.addOnPositiveButtonClickListener { selection ->
            // 取得選擇的日期
            val selectedDate = Date(selection)

            // 格式化日期
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate)

            // 更新 tv_start_date 的文字
//            binding.tvStartDate.text = formattedDate
            viewModel.setNotificationSettingStartDate(formattedDate)
        }

        // 顯示 MaterialDatePicker 對話框
        picker.show(parentFragmentManager, "datePicker")
    }

}
