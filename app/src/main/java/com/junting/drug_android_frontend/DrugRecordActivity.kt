package com.junting.drug_android_frontend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.junting.drug_android_frontend.databinding.ActivityDrugRecordBinding
import com.junting.drug_android_frontend.model.drug_record.DrugRecord
import com.junting.drug_android_frontend.model.drug_record.InteractingDrug
import com.junting.drug_android_frontend.model.drugbag_info.DrugbagInformation
import com.junting.drug_android_frontend.ui.libs.ExpandableListUtils
import java.util.*
import com.junting.drug_android_frontend.ui.drugRecords.DrugRecordsViewModel

class DrugRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrugRecordBinding

    internal var adapter: RrugRecordExpandableListAdapter? = null

    private var viewModel: DrugRecordsViewModel = DrugRecordsViewModel()

    private var checkBoxes: Array<CheckBox> = arrayOf()

    private var drugRecordId: Int? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrugRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        checkBoxes = arrayOf<CheckBox>(
            binding.cbAfterMeal,
            binding.cbAfterMeal,
            binding.cbWithFood,
            binding.cbBeforeSleep
        )


        DialogUtils.initTextViewEditDialog(this,binding.llDrugName, binding.tvDrugName,"修改藥物名稱",false) { text ->
            viewModel.setDrugName(text)
        }
        DialogUtils.initTextViewEditDialog(this,binding.llHospital, binding.tvHospital, "修改醫院名稱",false){
            text -> viewModel.setHospitalName(text)
        }
        DialogUtils.initTextViewEditDialog(this,binding.llDepartment, binding.tvDepartment, "修改科別名稱",false){
            text -> viewModel.setHospitalDepartment(text)
        }
        DialogUtils.initTextViewEditDialog(this,binding.llPhoneExtension, binding.tvPhoneExtension, "修改電話與分機",true){
            text -> val parts = text.split("-")
            viewModel.setHospitalPhone(parts[0])
            viewModel.setExtension(parts[1])
        }
        DialogUtils.initTextViewEditDialog(this,binding.llIndication, binding.tvIndication, "修改適應症",false){
            text -> viewModel.setIndication(text)
        }
        DialogUtils.initTextViewEditDialog(this,binding.llIndicationTag, binding.tvIndicationTag, "修改適應症-標籤",false){
                text -> viewModel.setIndicationTag(text)
        }
        DialogUtils.initTextViewEditDialog(this,binding.llSideEffect, binding.tvSideEffect, "修改副作用",false){
            text -> viewModel.setSideEffect(text)
        }
        DialogUtils.initTextViewEditDialog(this,binding.llAppearance, binding.tvAppearance, "修改外觀",false){
            text -> viewModel.setAppearance(text)
        }
        DialogUtils.initTextViewEditDialog(this,binding.llDosage, binding.tvDosage, "修改劑量",true){
            text -> viewModel.setDosage(text.toInt())
        }
        DialogUtils.initTextViewEditDialog(this,binding.llStock, binding.tvStock, "修改庫存",true){
            text -> viewModel.setStock(text.toInt())
        }
        initOndemandCheckbox()
        initTimingsCheckbox()
        initButtonSheet(binding.llNotificationSetting, NotificationSettingButtonSheet(viewModel), "notificationSetting")
        initButtonSheet(binding.llReturnSetting, ReturnSettingButtonSheet(viewModel), "returnSetting")
        initButtonSheet(binding.llDrugPosition, DrugPositionButtonSheet(viewModel), "drugPosition")
        initButton()
        initPhoneLongClickCall()
        //代表前一個動作一點選卡片
        drugRecordId = intent.getIntExtra("drugRecordId", 0)
        if (drugRecordId == 0) {
            getObjFromPreviousActivity()
        } else {
            initDrugRecordViewModel()
        }


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
                        intent.putExtra("fragmentName", "DrugRecordsFragment")
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
        menuInflater.inflate(R.menu.menu_pause_delete, menu)

        // 根據條件控制菜單項目的顯示與隱藏
//        val drugRecordId = intent.getIntExtra("drugRecordId", 0)
//        val deleteMenuItem = menu?.findItem(R.id.action_delete)
//        deleteMenuItem?.isVisible = drugRecordId != 0

        return true
    }
    private fun initPhoneLongClickCall() {
        binding.llPhoneExtension.setOnLongClickListener {
            val phone = viewModel.record.value?.hospital?.phone
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            startActivity(intent)
            true
        }
    }

    private fun initButtonSheet(layout: ViewGroup, buttonSheet: BottomSheetDialogFragment, tag: String) {
        layout.setOnClickListener{
            buttonSheet.show(supportFragmentManager, tag)
        }
    }
    private fun getObjFromPreviousActivity(){
        viewModel.record.value = DrugRecord()

        if (intent.getSerializableExtra("drugInteractions") != null) {
            val drugInteractions = intent.getSerializableExtra("drugInteractions") as List<InteractingDrug>
//            initExpandableListInteraction(drugInteractions)
            Log.d("InteractingDrugs", "drugInteractions: ${drugInteractions}")

            viewModel.setInteractingDrugs(drugInteractions)
            initExpandableListInteraction(drugInteractions)
        }
        if (intent.getSerializableExtra("drugbagInfo") != null) {
            val drugbagInfo = intent.getSerializableExtra("drugbagInfo") as DrugbagInformation
            Log.d("DrugbagInformation", "drugbagInfo: ${drugbagInfo}")

            viewModel.setDrugName(drugbagInfo.drug.name)
            viewModel.setHospitalName(drugbagInfo.hospital.name)
            viewModel.setHospitalDepartment(drugbagInfo.hospital.department)
            viewModel.setHospitalPhone(drugbagInfo.hospital.phone)
            viewModel.setIndication(drugbagInfo.drug.indication)
            viewModel.setSideEffect(drugbagInfo.drug.sideEffect)
            viewModel.setAppearance(drugbagInfo.drug.appearance)
            viewModel.setOnDemand(drugbagInfo.onDemand)
            var timeSlots = mutableListOf<String>()
            when(drugbagInfo.frequency){
                1 -> timeSlots = mutableListOf("08:00")
                2 -> timeSlots = mutableListOf("08:00", "12:00")
                3 -> timeSlots = mutableListOf("08:00", "12:00", "18:00")
                4 -> timeSlots = mutableListOf("08:00", "12:00", "18:00", "22:00")
                5 -> timeSlots = mutableListOf("08:00", "12:00", "18:00", "22:00", "24:00")
            }
            viewModel.setTimeSlots(timeSlots)
            initTimeSection(timeSlots)

            viewModel.setTimings(drugbagInfo.timings)
            for (i in drugbagInfo.timings) {
                checkBoxes[i].isChecked = true
            }
            viewModel.setDosage(drugbagInfo.dosage)
            viewModel.setStock(drugbagInfo.stock)
        }
    }

    private fun initDrugRecordViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchRecordById(drugRecordId!!)
        viewModel.record.observe(this, Observer {
            Log.d("Observe DrugRecord", "record: ${it.toString()}")
            var timeSlots = it.timeSlots.toMutableList()
            initTimeSection(timeSlots)
            initExpandableListInteraction(it.interactingDrugs!!)
            for (i in it.timings) {
                checkBoxes[i].isChecked = true
            }
            binding.progressBar.visibility = GONE
        })
    }

    private fun initTimeSection(timeSlots : MutableList<String>) {

        binding.llTimeSlot.removeAllViews() // Remove all existing views before adding new ones

        for (i in timeSlots.indices) {
            val tvTimeSlot = binding.llTimeSlot.getChildAt(i) as? AppCompatTextView
                ?: AppCompatTextView(this)

            tvTimeSlot.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
//            tvTimeSlot.background = ContextCompat.getDrawable(this, R.attr.selectableItemBackground)
            val iconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_outline_delete_24)
            tvTimeSlot.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                iconDrawable,
                null
            )
            tvTimeSlot.setPadding(10, 10, 10, 10)
            tvTimeSlot.text = timeSlots[i]
//            tvTimeSlot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

            tvTimeSlot.setOnClickListener {
                timeSlots.removeAt(i)
                binding.llTimeSlot.removeView(tvTimeSlot)
                viewModel.setTimeSlots(timeSlots)
            }

            if (tvTimeSlot.parent == null) {
                binding.llTimeSlot.addView(tvTimeSlot)
            }
        }
        binding.tvTimeSlotAdd.setOnClickListener {
            val timeList = mutableListOf<String>()
            for (i in 0..23) {
                timeList.add("${i.toString().padStart(2, '0')}:00")
            }

            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("請選擇時間")
            builder.setItems(timeList.toTypedArray()) { dialog, which ->
                val time = timeList[which]

                // check if the time already exists
                if (timeSlots.contains(time)) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("警告")
                        .setMessage("所選時間已存在。")
                        .setPositiveButton("確定", null)
                        .show()
                    return@setItems
                }

                // add the new time to the list
                timeSlots.add(time)

                // sort the time list
                Collections.sort(timeSlots)

                // update the time slots in the view model
                viewModel.setTimeSlots(timeSlots)

                // remove all the views in the LinearLayout
                binding.llTimeSlot.removeAllViews()

                // add all the time slots again to the LinearLayout
                for (timeSlot in timeSlots) {
                    val tvTimeSlot = AppCompatTextView(this)
                    tvTimeSlot.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val iconDrawable =
                        ContextCompat.getDrawable(this, R.drawable.ic_outline_delete_24)
                    tvTimeSlot.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        iconDrawable,
                        null
                    )
                    tvTimeSlot.setPadding(10, 10, 10, 10)
                    tvTimeSlot.text = timeSlot
//                    tvTimeSlot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    binding.llTimeSlot.addView(tvTimeSlot)
                    tvTimeSlot.setOnClickListener {
                        timeSlots.remove(timeSlot)
                        binding.llTimeSlot.removeView(tvTimeSlot)
                    }
                }
            }
            builder.show()

        }
    }



    private fun initTimingsCheckbox() {
        binding.cbBeforeMeal.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.record.value?.let {
                if (isChecked) {
                    // 新增 0 到 timings 列表
                    it.timings = it.timings.toMutableSet().apply { add(0) }
                } else {
                    // 移除 timings 列表中的 0
                    it.timings = it.timings.toMutableSet().apply { remove(0) }
                }
            }
            binding.cbAfterMeal.isEnabled = !isChecked
            binding.cbWithFood.isEnabled = !isChecked
        }

        binding.cbAfterMeal.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.record.value?.let {
                if (isChecked) {
                    // 新增 1 到 timings 列表
                    it.timings = it.timings.toMutableSet().apply { add(1) }
                } else {
                    // 移除 timings 列表中的 1
                    it.timings = it.timings.toMutableSet().apply { remove(1) }
                }
            }
            binding.cbBeforeMeal.isEnabled = !isChecked
            binding.cbWithFood.isEnabled = !isChecked
        }

        binding.cbWithFood.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.record.value?.let {
                if (isChecked) {
                    // 新增 2 到 timings 列表
                    it.timings = it.timings.toMutableSet().apply { add(2) }
                } else {
                    // 移除 timings 列表中的 2
                    it.timings = it.timings.toMutableSet().apply { remove(2) }
                }
            }
            binding.cbBeforeMeal.isEnabled = !isChecked
            binding.cbAfterMeal.isEnabled = !isChecked
        }
        binding.cbBeforeSleep.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.record.value?.let {
                if (isChecked) {
                    // 新增 3 到 timings 列表
                    it.timings = it.timings.toMutableSet().apply { add(3) }
                } else {
                    // 移除 timings 列表中的 3
                    it.timings = it.timings.toMutableSet().apply { remove(3) }
                }
            }
        }
    }

    private fun initOndemandCheckbox() {
        binding.llOnDemand.setOnClickListener {
            binding.cbOnDemand.isChecked = !binding.cbOnDemand.isChecked
        }
        binding.cbOnDemand.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.llOuterborderTimeSlot.visibility = GONE
                binding.llTimings.visibility = GONE
                viewModel.setOnDemand(true)
            } else {
                binding.llOuterborderTimeSlot.visibility = View.VISIBLE
                binding.llTimings.visibility = View.VISIBLE
                viewModel.setOnDemand(false)
            }
        }
    }

    private fun initExpandableListInteraction(interactingDrugs: List<InteractingDrug>) {
        adapter = RrugRecordExpandableListAdapter(this, interactingDrugs)
        if (interactingDrugs.isEmpty()) {
            binding.expandableListInteraction!!.visibility = GONE
        }
        binding.expandableListInteraction!!.setAdapter(adapter)
        ExpandableListUtils.setupExpandHeight(binding.expandableListInteraction!!, adapter!!)
    }

    private fun initButton() {
        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentName", "DrugRecordsFragment")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        binding.btnConfirm.setOnClickListener {
            Log.d("DrugRecord",viewModel.record.value.toString())
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("fragmentName", "DrugRecordsFragment")
            startActivity(intent)
        }
    }
}
