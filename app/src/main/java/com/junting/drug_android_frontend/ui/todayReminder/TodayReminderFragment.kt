package com.junting.drug_android_frontend.ui.todayReminder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.junting.drug_android_frontend.MainActivity
import com.junting.drug_android_frontend.OnDemandListActivity
import com.junting.drug_android_frontend.PillBoxViewManager
import com.junting.drug_android_frontend.R
import com.junting.drug_android_frontend.databinding.FragmentPillBoxManagementBinding
import com.junting.drug_android_frontend.databinding.FragmentTodayReminderBinding
import com.junting.drug_android_frontend.model.take_record.TakeRecord
import com.junting.drug_android_frontend.model.today_reminder.TodayReminder
import com.junting.drug_android_frontend.services.BTServices.BluetoothSocket
import com.junting.drug_android_frontend.ui.libs.updater.UpdateUIHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TodayReminderFragment : Fragment() {

    private var _binding: FragmentTodayReminderBinding? = null
    private lateinit var bindingPillBox: FragmentPillBoxManagementBinding
    private lateinit var viewAdapter: TodayReminderViewAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var viewModel: TodayReminderViewModel = TodayReminderViewModel()
    private lateinit var pillBoxViewManager: PillBoxViewManager
    private val positions = (1..9).toList()

    private val onItemSwipeListener = object : OnItemSwipeListener<TodayReminder> {
        override fun onItemSwiped(
            position: Int,
            direction: OnItemSwipeListener.SwipeDirection,
            item: TodayReminder,
        ): Boolean {
            if (direction == OnItemSwipeListener.SwipeDirection.RIGHT_TO_LEFT) {
                Log.d("onItemSwiped", "向左滑: $position, $item")

                val takeRecord = TakeRecord(
                    todayReminderId = item.id,
                    status = 2
                )
                viewModel.viewModelScope.launch {
                    val responseMessage = viewModel.processTakeRecord(takeRecord).await()
                    if (responseMessage != null) {
                        Toast.makeText(requireContext(), "已略過", Toast.LENGTH_SHORT).show()
                        // 成功處理 TakeRecord
                        viewModel.fetchTodayReminders()
                    } else {
                        // 處理失敗
                        Toast.makeText(requireContext(), "系統錯誤", Toast.LENGTH_SHORT).show()
                        // ... 處理失敗的邏輯 ...
                    }
                }

            } else if (direction == OnItemSwipeListener.SwipeDirection.LEFT_TO_RIGHT) {
                Log.d("onItemSwiped", "向右滑: $position, $item")

                val currentTime = Calendar.getInstance()
                val currentTimeString =
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime.time)

                val takeRecord = TakeRecord(
                    todayReminderId = item.id,
                    status = 1,
                    dosage = item.dosage,
                    timeSlot = currentTimeString
                )
                viewModel.viewModelScope.launch {
                    val responseMessage = viewModel.processTakeRecord(takeRecord).await()
                    if (responseMessage != null) {

                        val bs = BluetoothSocket()
                        lifecycleScope.launch(Dispatchers.IO) {
                            bs.openPillbox(item.position.toString())
                        }

                        Toast.makeText(requireContext(), "服用成功", Toast.LENGTH_SHORT).show()
                        // 成功處理 TakeRecord

                        val parentView = bindingPillBox.root.parent as? ViewGroup
                        parentView?.removeView(bindingPillBox.root)
                        pillBoxViewManager.setCellColor(item.position)

                        val dialog = MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.taken_drug))
                            .setView(bindingPillBox.root)
                            .setPositiveButton(resources.getString(R.string.close_pillbox)) { dialog, which ->
                                Log.d("Bosh here", "close pillbox position: ${item.position}")

                                lifecycleScope.launch(Dispatchers.IO) {
                                    bs.closePillbox(item.position.toString())
                                }
                            }
                            .create()

                        dialog.show()


                        viewModel.fetchTodayReminders()
                    } else {
                        // 處理失敗
                        Toast.makeText(requireContext(), "系統錯誤", Toast.LENGTH_SHORT).show()
                        // ... 處理失敗的邏輯 ...
                    }
                }
            }
            // 處理項目被滑動的動作
            // 返回 false 表示滑動的項目應該從適配器的資料集中移除（預設行為）
            // 返回 true 表示停止滑動的項目自動從適配器的資料集中移除（在這種情況下，你需要自行手動更新資料集）
//            updateTodayReminderBadge(viewAdapter.itemCount)
            return false
        }
    }
    private val timeRanges = generateTimeRanges()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTodayReminderBinding.inflate(inflater, container, false)
        bindingPillBox = FragmentPillBoxManagementBinding.inflate(layoutInflater)
        val root: View = binding.root
        pillBoxViewManager =
            PillBoxViewManager(bindingPillBox, requireContext()) // Initialize the view manager

        // 隱藏所有藥物位置
        positions.forEach { i -> pillBoxViewManager.hideCell(i, positions) }


        initRecyclerView()
        initRecyclerViewModel()
        initChooseTime()
        initFab()
        initCheckIcon()
        // 設定 onItemSwipeListener
        binding.list.swipeListener = onItemSwipeListener

        val inflater = LayoutInflater.from(requireContext())
        val instructionLayout =
            inflater.inflate(R.layout.instruction_background, bindingPillBox.llInstruction, false)
        bindingPillBox.llInstruction.addView(instructionLayout)
        return root
    }

    private fun generateTimeRanges(): Array<String> {
        val timeRanges = mutableListOf<String>()

        for (hour in 0..23) {
            val timeRange = String.format("%02d:00~%02d:59", hour, hour)
            timeRanges.add(timeRange)
        }

        return timeRanges.toTypedArray()
    }

    private fun initChooseTime() {
        binding.inputLayout.isEndIconVisible = false
        binding.inputEditText.setOnClickListener {
            showTimeRangeDialog()
        }
        binding.inputLayout.setEndIconOnClickListener {
            val selectedTimeRange = binding.inputEditText.text.toString()
            Log.d("SelectedTimeRange", selectedTimeRange)

        }
    }

    private fun showTimeRangeDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(resources.getString(R.string.select_time))
        builder.setItems(timeRanges) { _, index ->
            val selectedTimeRange = timeRanges[index]
            binding.inputEditText.setText(selectedTimeRange)
            binding.inputLayout.isEndIconVisible = true
        }
        builder.create().show()
    }

    private fun initCheckIcon() {
        val inputLayout = binding.inputLayout

        val endIconView =
            inputLayout.findViewById<ImageView>(com.google.android.material.R.id.text_input_end_icon)
        endIconView?.setOnClickListener {
            val selectedTimeRange = binding.inputEditText.text.toString()
            Log.d("SelectedTimeRange", selectedTimeRange)

            val firstTwoDigits = selectedTimeRange.substring(0, 2).trim().toInt()
            val currentTime = Calendar.getInstance()
            val currentTimeString =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime.time)

            val takeRecord = TakeRecord(
                batchTime = firstTwoDigits,
                status = 4,
                timeSlot = currentTimeString
            )
            Log.d("TakeRecord", takeRecord.toString())

            // 使用viewModelScope的IO上下文执行网络请求
            viewModel.viewModelScope.launch() {
                try {
                    val responseMessage = viewModel.processTakeRecord(takeRecord).await()

                    if (responseMessage != null && responseMessage.success) {

                        for(p in responseMessage.positions) {
                            pillBoxViewManager.setCellColor(p)
                        }

                        val bs = BluetoothSocket()
                        for (p in responseMessage.positions){
                            lifecycleScope.launch(Dispatchers.IO) {
                                bs.openPillbox(p.toString())
                            }
                        }

                        val builder = MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.taken_drug))
                            .setMessage("$selectedTimeRange 區段之藥物服用成功")
                            .setView(bindingPillBox.root)
                            .setPositiveButton(resources.getString(R.string.close_pillbox)) { dialog, which ->
                                Log.d("Bosh here", "close pillbox positions: ${responseMessage.positions}")
                                // Handle positive button click
                                lifecycleScope.launch(Dispatchers.IO) {
                                    bs.closeMultiplePillbox(responseMessage.positions.toIntArray())
                                }
                                binding.inputLayout.isEndIconVisible = false
                            }
                            .create()

                        builder.show()
                        viewModel.fetchTodayReminders()

                    } else if (responseMessage != null && !responseMessage.success) {
                        val dialogMessage = when {
                            responseMessage.errorCode == 1001 -> "未找到 $selectedTimeRange 區段之藥物"
                            else -> "系統錯誤"
                        }
                        val builder = MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.hint_title))
                            .setMessage(dialogMessage)
                            .setPositiveButton(R.string.confirm) { _, _ ->
                                // Handle positive button click
                                binding.inputLayout.isEndIconVisible = false
                            }.create()
                        builder.show()
                    }
                } catch (e: Exception) {
                    // 处理异常情况
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchTodayReminders()
        viewModel.todayReminders.observe(context as AppCompatActivity, Observer {
            updateTodayReminderBadge(it.size)
            viewAdapter!!.update(it)
            binding.progressBar.visibility = View.GONE
        })

        viewModel.fetchDrugRecords()
        viewModel.drugRecors.observe(viewLifecycleOwner, Observer {

            // 遍历记录并更新 UI
            for (record in it) {
                when (record.position) {
                    1 -> pillBoxViewManager.showCell(1, record, false)
                    2 -> pillBoxViewManager.showCell(2, record, false)
                    3 -> pillBoxViewManager.showCell(3, record, false)
                    4 -> pillBoxViewManager.showCell(4, record, false)
                    5 -> pillBoxViewManager.showCell(5, record, false)
                    6 -> pillBoxViewManager.showCell(6, record, false)
                    7 -> pillBoxViewManager.showCell(7, record, false)
                    8 -> pillBoxViewManager.showCell(8, record, false)
                    9 -> pillBoxViewManager.showCell(9, record, false)
                }
            }
            positions.forEach { i -> pillBoxViewManager.closeProgressBar(i) }
        })
    }

    private fun initRecyclerView() {
        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = TodayReminderViewAdapter(requireContext())
        val apply = binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = viewAdapter
            behindSwipedItemBackgroundColor =
                ContextCompat.getColor(requireContext(), R.color.md_theme_light_outline)
            behindSwipedItemBackgroundSecondaryColor =
                ContextCompat.getColor(requireContext(), R.color.md_theme_dark_primary)
            behindSwipedItemIconDrawableId = R.drawable.ic_baseline_cancel_24
            behindSwipedItemIconSecondaryDrawableId = R.drawable.ic_baseline_check_circle_24
            behindSwipedItemIconMargin = 60.0f
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.todayReminders.observe(context as AppCompatActivity, Observer {
            UpdateUIHelper.notify(context, "setTodayReminderBadge", it.size)
        })
    }

    fun updateTodayReminderBadge(number: Int) {
        val mainActivity: MainActivity? = activity as? MainActivity
        mainActivity?.setTodayReminderBadge(number) // 設定小紅點圖標數字
    }

    private fun initFab() {
        binding.fab.setOnClickListener {
            val intent = Intent(context, OnDemandListActivity::class.java)
            context?.startActivity(intent)
        }
    }
}
