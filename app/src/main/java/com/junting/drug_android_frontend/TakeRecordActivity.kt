package com.junting.drug_android_frontend

import DialogUtils
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.junting.drug_android_frontend.databinding.ActionBarTakeRecordTodayReminderBinding
import com.junting.drug_android_frontend.databinding.ActivityTakeRecordBinding
import com.junting.drug_android_frontend.databinding.ActivityTodayReminderBinding
import com.junting.drug_android_frontend.databinding.BottomSheetLaterBinding
import com.junting.drug_android_frontend.databinding.FragmentPillBoxManagementBinding
import com.junting.drug_android_frontend.ui.takeRecords.TakeRecordsViewModel
import com.junting.drug_android_frontend.ui.todayReminder.TodayReminderViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TakeRecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTakeRecordBinding
    var takeRecordId: Int? = null
    private var viewModel: TakeRecordsViewModel = TakeRecordsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeRecordBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        takeRecordId = intent.getSerializableExtra("takeRecordId") as Int?
        if (takeRecordId != null) {
            this.takeRecordId = takeRecordId
            initViewModel()
        }

        initButton()
        initClickableTextView()


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_delete -> {
                // 处理点击删除按钮的逻辑
                val alertDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("詢問")
                    .setMessage("確認要刪除?")
                    .setPositiveButton("確認") { dialog, _ ->
                        // 在這裡執行刪除操作
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("fragmentName", "TakeRecordsFragment")
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()

                    }
                    .create()
                alertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
        return true
    }

    private fun initViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchTakeRecordById(takeRecordId!!)
        viewModel.takeRecord.observe(this, Observer {
            Log.d("Observe takeRecord", "record: ${it.toString()}")
            supportActionBar?.setTitle(it.drug.name)
            initSpinner()
            binding.spinnerStatus.setSelection(it.status)
            binding.spinnerStatus.selectedItemPosition
            binding.progressBar.visibility = View.GONE
        })

    }
    private fun initSpinner() {
        var isSpinnerInitialized = false
        val options = arrayOf("未知", "已服用", "未服用")
        val adapter = StatusSpinnerAdapter(this, options)
        binding.spinnerStatus.adapter = adapter
        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("onNothingSelected", "onNothingSelected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialized) {
                    Log.d("onItemSelected", "onItemSelected")
                    viewModel.setStatus(position)
                } else {
                    isSpinnerInitialized = true
                }
            }
        }

    }

    private fun initClickableTextView() {
        DialogUtils.initTextViewEditDialog(this,binding.llDosage,binding.tvDosage, "劑量", true) {
                text -> viewModel.setDosage(text.toInt())
        }
        binding.llTimeSlot.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePicker = MaterialTimePicker.Builder()
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("選擇服藥時間")
                .setPositiveButtonText("確定")
                .setNegativeButtonText("取消")
                .build()


            timePicker.addOnPositiveButtonClickListener {
                val selectedHour = timePicker.hour
                val selectedMinute = timePicker.minute

                viewModel.setTimeSlot(
                    String.format(
                        "%02d:%02d",
                        selectedHour,
                        selectedMinute
                    )
                )
            }

            timePicker.show(supportFragmentManager, "timePicker")
        }
        binding.llDate.setOnClickListener {
            // 使用 MaterialDatePicker 顯示日期選擇對話框
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder
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

                // 更新 date 的文字
                viewModel.setDate(formattedDate)
            }

            // 顯示 MaterialDatePicker 對話框
            picker.show(supportFragmentManager, picker.toString())
        }

    }



    private fun initButton() {
        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }
        binding.btnConfirm.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("fragmentName", "TakeRecordsFragment")
            startActivity(intent)
        }
    }

}