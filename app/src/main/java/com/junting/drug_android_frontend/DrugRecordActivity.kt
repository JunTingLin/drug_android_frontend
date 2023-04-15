package com.junting.drug_android_frontend

import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.junting.drug_android_frontend.databinding.ActivityDrugRecordBinding
import com.junting.drug_android_frontend.model.drug_record.InteractingDrug
import com.junting.drug_android_frontend.ui.libs.ExpandableListUtils
import java.util.*

class DrugRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrugRecordBinding

    internal var adapter: EditRrugExpandableListAdapter? = null

    private lateinit var viewModel: DrugRecordViewModel

    private var timeSlots = mutableListOf<String>()

    private var checkBoxes: Array<CheckBox> = arrayOf()

    private var drugId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrugRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        checkBoxes = arrayOf<CheckBox>(
            binding.cbAfterMeal,
            binding.cbAfterMeal,
            binding.cbWithFood,
            binding.cbBeforeSleep
        )

        drugId = intent.getIntExtra("drugId", 0)
        initDrugRecordViewModel()
        initDrugNameTextView()
        initOndemandCheckbox()
        initTimingsCheckbox()
        initButton()
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

    private fun initDrugRecordViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel = DrugRecordViewModel()
        viewModel.fetchRecord(drugId!!)
        viewModel.record.observe(this, Observer {
            binding.tvDrugName.text = it.drug.name
            binding.tvHospital.text = it.hospitalName
            binding.tvDepartment.text = it.hospitalDepartment
            binding.tvIndication.text = it.drug.indications
            binding.tvSideEffect.text = it.drug.sideEffect
            binding.tvAppearance.text = it.drug.appearance
            binding.cbOnDemand.isChecked = it.onDemand
            timeSlots = it.timeSlots.toMutableList()
            initTimeSection()
            initExpandableListInteraction(it.interactingDrugs!!)
            for (i in it.timings) {
                checkBoxes[i].isChecked = true
            }
            binding.tvDosage.text = it.dosage.toString()
            binding.tvStock.text = it.stock.toString()

            binding.progressBar.visibility = View.GONE

        })
    }

    private fun initTimeSection() {

        for (timeSlot in timeSlots) {
            val tvTimeSlot = AppCompatTextView(this)
            tvTimeSlot.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
//            tvTimeSlot.background = ContextCompat.getDrawable(this, android.R.attr.selectableItemBackground)
            val iconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_outline_delete_24)
            tvTimeSlot.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                iconDrawable,
                null
            )
            tvTimeSlot.setPadding(10, 10, 10, 10)
            tvTimeSlot.text = timeSlot
            tvTimeSlot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            binding.llTimeSlot.addView(tvTimeSlot)
            tvTimeSlot.setOnClickListener {
                timeSlots.remove(timeSlot)
                binding.llTimeSlot.removeView(tvTimeSlot)
            }
        }
        binding.tvTimeSlotAdd.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .build()

            picker.addOnPositiveButtonClickListener {
                val hour = picker.hour.toString().padStart(2, '0')
                val minute = picker.minute.toString().padStart(2, '0')
                val time = "$hour:$minute"

                // check if the time already exists
                if (timeSlots.contains(time)) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Warning")
                        .setMessage("The selected time already exists.")
                        .setPositiveButton("OK", null)
                        .show()
                    return@addOnPositiveButtonClickListener
                }

                // add the new time to the list
                timeSlots.add(time)

                // sort the time list
                Collections.sort(timeSlots)

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
                    tvTimeSlot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    binding.llTimeSlot.addView(tvTimeSlot)
                    tvTimeSlot.setOnClickListener {
                        timeSlots.remove(timeSlot)
                        binding.llTimeSlot.removeView(tvTimeSlot)
                    }
                }
            }

            picker.show(supportFragmentManager, "time_picker")
        }
    }

    private fun initDrugNameTextView() {
        binding.llDrugName.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("修改藥物名稱")

            // 建立一個 EditText 供使用者輸入新的藥物名稱
            val input = EditText(this)
            input.setText(binding.tvDrugName.text)
            builder.setView(input)

            // 設定確認和取消按鈕
            builder.setPositiveButton("確定") { dialog, which ->
                binding.tvDrugName.text = input.text.toString()
            }
            builder.setNegativeButton("取消") { dialog, which ->
                dialog.cancel()
            }

            // 顯示對話框
            builder.show()
        }
    }

    private fun initTimingsCheckbox() {
        binding.cbBeforeMeal.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.cbAfterMeal.isEnabled = !isChecked
            binding.cbWithFood.isEnabled = !isChecked
        }

        binding.cbAfterMeal.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.cbBeforeMeal.isEnabled = !isChecked
            binding.cbWithFood.isEnabled = !isChecked
        }

        binding.cbWithFood.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.cbBeforeMeal.isEnabled = !isChecked
            binding.cbAfterMeal.isEnabled = !isChecked
        }
    }

    private fun initOndemandCheckbox() {
        binding.llOnDemand.setOnClickListener {
            binding.cbOnDemand.isChecked = !binding.cbOnDemand.isChecked
        }
        binding.cbOnDemand.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.llOuterborderTimeSlot.visibility = View.GONE
                binding.llTimings.visibility = View.GONE
            } else {
                binding.llOuterborderTimeSlot.visibility = View.VISIBLE
                binding.llTimings.visibility = View.VISIBLE
            }
        }
    }

    private fun initExpandableListInteraction(interactingDrugs: List<InteractingDrug>) {
        adapter = EditRrugExpandableListAdapter(this, interactingDrugs)
        if (interactingDrugs.isEmpty()) {
            binding.expandableListInteraction!!.visibility = GONE
        }
        binding.expandableListInteraction!!.setAdapter(adapter)
        ExpandableListUtils.setupExpandHeight(binding.expandableListInteraction!!, adapter!!)
    }

    private fun initButton() {
        binding.btnCancel.setOnClickListener {
            super.onBackPressed()
        }
        binding.btnConfirm.setOnClickListener {
            super.onBackPressed()
        }
    }
}