package com.junting.drug_android_frontend

import DialogUtils
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.junting.drug_android_frontend.databinding.ActionBarTakeRecordTodayReminderBinding
import com.junting.drug_android_frontend.databinding.ActivityOnDemandBinding
import com.junting.drug_android_frontend.databinding.FragmentPillBoxManagementBinding
import com.junting.drug_android_frontend.model.take_record.TakeRecord
import com.junting.drug_android_frontend.services.BTServices.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OnDemandActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnDemandBinding
    private lateinit var bindingPillBox: FragmentPillBoxManagementBinding
    private lateinit var bindingActionBarTakeRecordTodayReminder: ActionBarTakeRecordTodayReminderBinding
    var drugRecordId: Int? = null
    private var viewModel: OnDemandViewModel = OnDemandViewModel()
    private lateinit var pillBoxViewManager: PillBoxViewManager
    private val positions = (1..9).toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnDemandBinding.inflate(layoutInflater)
        bindingPillBox = FragmentPillBoxManagementBinding.inflate(layoutInflater)
        bindingActionBarTakeRecordTodayReminder = ActionBarTakeRecordTodayReminderBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)
        pillBoxViewManager = PillBoxViewManager(bindingPillBox,this) // Initialize the view manager

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
//        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)  //完全自定義
        supportActionBar?.setTitle("")
        supportActionBar?.setCustomView(bindingActionBarTakeRecordTodayReminder.root)

        drugRecordId = intent.getSerializableExtra("drugRecordId") as Int?
        if (drugRecordId != null) {
            this.drugRecordId = drugRecordId
            initViewModel()
        }
        // 隱藏所有藥物位置
        positions.forEach { i -> pillBoxViewManager.hideCell(i,positions) }
        initActualTime()
        initButton()
        initClickableTextView()
    }
    private fun initViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchDrugRecordById(drugRecordId!!)
        viewModel.drugRecord.observe(this, Observer {
            Log.d("Observe todayReminder", "record: ${it.toString()}")
            bindingActionBarTakeRecordTodayReminder.tvDrugName.text = it.drug.name
            bindingActionBarTakeRecordTodayReminder.tvStock.text = resources.getString(R.string.stock)+"："+it.stock.toString()+" "+resources.getString(R.string.unit)
            pillBoxViewManager.setCellColor(it.position)
            binding.progressBar.visibility = View.GONE
        })
        viewModel.fetchDrugRecords()
        viewModel.drugRecors.observe(this, Observer {

            // 遍历记录并更新 UI
            for (record in it) {
                when (record.position) {
                    1 -> pillBoxViewManager.showCell(1, record,false)
                    2 -> pillBoxViewManager.showCell(2, record,false)
                    3 -> pillBoxViewManager.showCell(3, record,false)
                    4 -> pillBoxViewManager.showCell(4, record,false)
                    5 -> pillBoxViewManager.showCell(5, record,false)
                    6 -> pillBoxViewManager.showCell(6, record,false)
                    7 -> pillBoxViewManager.showCell(7, record,false)
                    8 -> pillBoxViewManager.showCell(8, record,false)
                    9 -> pillBoxViewManager.showCell(9, record,false)
                }
            }
            positions.forEach { i -> pillBoxViewManager.closeProgressBar(i) }
        })

        val inflater = LayoutInflater.from(this)
        val instructionLayout = inflater.inflate(R.layout.instruction_background, bindingPillBox.llInstruction, false)
        bindingPillBox.llInstruction.addView(instructionLayout)
    }

    private fun initClickableTextView() {
        DialogUtils.initTextViewEditDialog(
            this,
            binding.llDosage,
            binding.tvDosage,
            resources.getString(R.string.dosage),
            true
        ) { text ->
            viewModel.setDosage(text.toInt())
        }
        binding.llTimeSlot.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePicker = MaterialTimePicker.Builder()
                .setHour(hour)
                .setMinute(minute)
                .setTitleText(resources.getString(R.string.take_record_select_time))
                .setPositiveButtonText(resources.getString(R.string.confirm))
                .setNegativeButtonText(resources.getString(R.string.dialog_cancel))
                .build()


            timePicker.addOnPositiveButtonClickListener {
                val selectedHour = timePicker.hour
                val selectedMinute = timePicker.minute

                viewModel.setActualTakingTime(
                    String.format(
                        "%02d:%02d",
                        selectedHour,
                        selectedMinute
                    )
                )
            }

            timePicker.show(supportFragmentManager, "timePicker")
        }
    }


    private fun initActualTime() {
        val currentTime = Calendar.getInstance()
        viewModel.actualTakingTime.set(
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                currentTime.time
            )
        )
    }

    private fun initButton() {
        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("fragmentName", "TodayReminderFragment")
            startActivity(intent)
        }
        binding.btnConfirm.setOnClickListener{
            showConfirmDialog()
        }
    }



    private fun showConfirmDialog() {
        var takeRecord = TakeRecord(
            drugRecordId = viewModel.drugRecord.value!!.id,
            status = 5,
            dosage = viewModel.drugRecord.value!!.dosage,
            timeSlot = viewModel.actualTakingTime.get()!!
        )
        Log.d("TakeRecord", "takeRecord: ${takeRecord.toString()}")

        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val responseMessage = viewModel.processTakeRecord(takeRecord).await()
            if (responseMessage != null) {
                runOnUiThread(Runnable {

                    val bs = BluetoothSocket()
                    lifecycleScope.launch(Dispatchers.IO) {
                        bs.openPillbox(viewModel.drugRecord.value!!.position.toString())
                    }

                    Toast.makeText(this@OnDemandActivity, "服用成功", Toast.LENGTH_SHORT).show()

                    val parentView = bindingPillBox.root.parent as? ViewGroup
                    parentView?.removeView(bindingPillBox.root)

                    val builder = MaterialAlertDialogBuilder(this@OnDemandActivity)
                        .setTitle(resources.getString(R.string.taken_drug))
                        .setView(bindingPillBox.root)
                        .setPositiveButton(resources.getString(R.string.close_pillbox)) { dialog, which ->
                            Log.d("Bosh here", "close pillbox position: ${viewModel.drugRecord.value!!.position}")

                            lifecycleScope.launch(Dispatchers.IO) {
                                bs.closePillbox(viewModel.drugRecord.value!!.position.toString())
                            }

                            val intent = Intent(this@OnDemandActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.putExtra("fragmentName", "TodayReminderFragment")
                            startActivity(intent)
                        }
                        .create()

                    builder.show()
                })
                // 成功處理 TakeRecord
            } else {
                // 處理失敗
                runOnUiThread(Runnable {
                    Toast.makeText(this@OnDemandActivity, "服用失敗", Toast.LENGTH_SHORT).show()
                })
                // ... 處理失敗的邏輯 ...
            }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}