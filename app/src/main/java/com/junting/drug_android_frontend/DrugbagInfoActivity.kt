package com.junting.drug_android_frontend

import android.R as MaterialR
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.junting.drug_android_frontend.databinding.ActivityDrugbagInfoBinding
import com.junting.drug_android_frontend.libs.SharedPreferencesManager
import com.junting.drug_android_frontend.model.UglyText
import com.junting.drug_android_frontend.model.drugbag_info.DrugbagInformation
import com.junting.drug_android_frontend.R as MyAppR

class DrugbagInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrugbagInfoBinding
    private var viewModel: DrugbagInfoViewModel = DrugbagInfoViewModel()
    private var checkBoxes: Array<CheckBox> = arrayOf()
    private var uglyText : UglyText? = null
    private val testData :String = "\"9.5\\\\nOSO\\\\nAM\\\\n姓名: 胡雅涵\\\\n(Name)\\\\n病歷號碼:\\\\n年齡:\\\\n22378331\\\\n(Chart No.)\\\\n(Age)\\\\n科別: 內分泌暨新陳醫師:\\\\n(Department)\\\\n(Doctor)\\\\n亞長庚紀念醫院\\\\nCHANG GUNG MEMORIAL HOSPITAL\\\\n網址: http://www.cgmh.org.tw\\\\n【藥品外觀】\\\\n【使用方法】\\\\n【臨床用途】\\\\n【副作用】\\\\n【預約回診】\\\\n44\\\\n蔡松昇\\\\n2023/03/08 08:52:10 HA84\\\\n領藥號碼 No.\\\\n生日:\\\\n(Date of Birth)978/09/10\\\\n性別:\\\\n男\\\\n(Sex)\\\\n體重:\\\\n(Body Weight)\\\\nA 10144 桃園\\\\n調劑日期:\\\\n(Dispensing Date)\\\\n【藥 名】 517(Eltroxin,GSK) Thyroxine sodium 0.1mg/tab\\\\n1\\\\n商品名: Eltroxin 昂特欣\\\\n廠牌: Aspen Pharmacare\\\\n本品建議在 2023/05/10 前用完\\\\n2023/03/08\\\\n張家雯\\\\n藥師:\\\\n(Pharmacist)\\\\n21#*2+14\\\\n白色圓形錠劑·有GS 21C 與 100字樣\\\\n內服藥,口服\\\\n每天1次,早飯前服用,每次2粒,28天份\\\\n1甲狀腺機能減退症\\\\n體重下降,頭痛,胃腸不適,食慾增加\\\\n2023/03/09星期四·復健科鄭如艾醫師上午診23號\\\\n2023/03/27星期一·復健科陳建宏醫師下午診19號\\\\n56 PC\\\\n2-1\\\\n『本單含個人資料\""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrugbagInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        checkBoxes = arrayOf<CheckBox>(
            binding.cbAfterMeal,
            binding.cbAfterMeal,
            binding.cbWithFood,
            binding.cbBeforeSleep
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (intent.getStringExtra("UglyTextString")?.isNotEmpty() == true) {
            uglyText = UglyText(intent.getStringExtra("UglyTextString")!!)  //創建UglyText物件
            supportActionBar?.setTitle(resources.getString(MyAppR.string.modify_drugbag_info))
            initDrugbagInfoViewModel()
        } else {
            supportActionBar?.setTitle(resources.getString(MyAppR.string.add_drugbag_info))
            userInputDrugbagInfo()
        }

        initOndemandCheckbox()
        initTimingsCheckbox()
        initButton()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MaterialR.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun userInputDrugbagInfo(){
        viewModel.drugbagInfo.value = DrugbagInformation()
        initFrequencyDropdown(false)
        initDosageDropdown(false)
    }

    private fun initDrugbagInfoViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        val fakeFlag = SharedPreferencesManager(this).getFakeFlag()
        if(fakeFlag) {
            viewModel.fetchDrugbagInfo()  //打做好的API假資料
        } else {
            viewModel.sendDrugbagInfo(uglyText = uglyText!!)  //送後端做AI提取真實關鍵字
        }
        viewModel.drugbagInfo.observe(this, Observer {
            binding.cbOnDemand.isChecked = it.onDemand

            initFrequencyDropdown(true,it.frequency)
            for (i in it.timings) {
                checkBoxes[i].isChecked = true
            }
            initDosageDropdown(true,it.dosage)

            binding.progressBar.visibility = View.GONE
        })

    }


    private fun initButton() {
        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentName", "DrugRecordsFragment")
            startActivity(intent)
        }
        binding.btnConfirm.setOnClickListener {
            val stock = viewModel.drugbagInfo.value?.stock ?: 0
            val dosage = viewModel.drugbagInfo.value?.dosage ?: 0
            val frequency = viewModel.drugbagInfo.value?.frequency ?: 0
            val isOnDemand = viewModel.drugbagInfo.value?.onDemand ?: false

            val dialogMessage = when {
                viewModel.drugbagInfo.value?.drug?.name.isNullOrEmpty() -> "藥物名稱不可為空"
                viewModel.drugbagInfo.value?.hospital?.name.isNullOrEmpty() -> "醫院名稱不可為空"
                viewModel.drugbagInfo.value?.hospital?.department.isNullOrEmpty() -> "科別名稱不可為空"
                (!isOnDemand && frequency == 0) -> "頻率(一天X次)不可為0或空"
                dosage == 0 -> "劑量不可為0或空"
                stock == 0 -> "庫存不可為0或空"
                (!isOnDemand && stock < (dosage * frequency)) -> "藥袋數量不足，請確認藥袋數量是否足夠"
                ( isOnDemand && stock < dosage ) -> "藥袋數量不足，請確認藥袋數量是否足夠"
                else -> "OK"
            }

            if(dialogMessage != "OK"){

                val builder = MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(MyAppR.string.warning_window_title))
                    .setMessage(dialogMessage)
                    .setPositiveButton(resources.getString(MyAppR.string.confirm)) { dialog, which ->
                        // Handle positive button click

                    }
                    .create()

                builder.show()
            }else{
                val intent = Intent(this, DrugInteractionActivity::class.java)
                viewModel.drugbagInfo.value?.let {
                    intent.putExtra("drugbagInfo", it)
                }
                startActivity(intent)
            }
        }
    }

    private fun initDosageDropdown(hasDefault: Boolean, defaultValue: Int=0) {
        if (hasDefault) {
            binding.actvDosage.setText(defaultValue.toString())
        }
        val dosageOption = arrayOf(1, 2, 3, 4, 5)
        val adapter = ArrayAdapter(this, MaterialR.layout.simple_spinner_item, dosageOption)
        binding.actvDosage.setAdapter(adapter)


        binding.actvDosage.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(this, resources.getString(MyAppR.string.select)+"：$selectedItem", Toast.LENGTH_SHORT).show()
                viewModel.drugbagInfo.value?.let {
                    it.dosage = selectedItem.toInt()
                }
            }
    }

    private fun initOndemandCheckbox() {
        binding.cbOnDemand.setOnCheckedChangeListener { _, isChecked ->
            binding.tilFrequency.visibility = if (isChecked) View.GONE else View.VISIBLE
            binding.llTimings.visibility = if (isChecked) View.GONE else View.VISIBLE
            viewModel.setOnDemand(isChecked)
        }

    }

    private fun initTimingsCheckbox() {
        binding.cbBeforeMeal.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.drugbagInfo.value?.let {
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
            viewModel.drugbagInfo.value?.let {
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
            viewModel.drugbagInfo.value?.let {
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
            viewModel.drugbagInfo.value?.let {
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


    private fun initFrequencyDropdown(hasDefault:Boolean,defaultValue: Int=1) {
        if(hasDefault){
            binding.actvFrequency.setText(defaultValue.toString())
        }
        val frequencyOption = arrayOf(1, 2, 3, 4, 5)
        val adapter = ArrayAdapter(this, MaterialR.layout.simple_spinner_item, frequencyOption)
        binding.actvFrequency.setAdapter(adapter)

        binding.actvFrequency.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(this, resources.getString(MyAppR.string.select)+"：$selectedItem", Toast.LENGTH_SHORT).show()
                viewModel.drugbagInfo.value?.let {
                    it.frequency = selectedItem.toInt()
                }
            }
    }

}
