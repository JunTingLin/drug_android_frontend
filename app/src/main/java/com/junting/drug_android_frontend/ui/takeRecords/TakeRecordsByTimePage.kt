package com.junting.drug_android_frontend.ui.takeRecords

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.junting.drug_android_frontend.R
import com.junting.drug_android_frontend.TakeRecordsByTimeExpandableListAdapter
import com.junting.drug_android_frontend.model.take_record.DateTimeSlotRecord


class TakeRecordsByTimePage(context: Context, container: ViewGroup) {

    val view: View
    private val context: Context
    private val container: ViewGroup
    private val progressBar: ProgressBar

    private val expandableListView: ExpandableListView
    private var expandableListAdapter: TakeRecordsByTimeExpandableListAdapter

    private val viewModel: TakeRecordsViewModel


    init {
        this.context = context
        this.container = container
        this.view =
            LayoutInflater.from(context).inflate(R.layout.take_records_drug_and_time_tab, container, false)
        this.progressBar = view.findViewById(R.id.progressBar)

        this.expandableListView = view.findViewById(R.id.expandable_list_view)
        this.expandableListAdapter = TakeRecordsByTimeExpandableListAdapter(context, listOf())

        this.viewModel = TakeRecordsViewModel()
        initViewModel()
    }
    private fun initViewModel() {
        this.viewModel.fetchTakeRecords()
        this.viewModel.dateTimeSlotRecords.observe(context as AppCompatActivity, Observer {
                dateTimeSlotRecords ->
            Log.d("TakeRecordsByTimePage", "dateTimeSlotRecords: $dateTimeSlotRecords")
            initExpandableList(dateTimeSlotRecords)
            progressBar.visibility = View.GONE
        })

    }
    private fun initExpandableList(dateTimeSlotRecords: List<DateTimeSlotRecord>) {
        expandableListAdapter = TakeRecordsByTimeExpandableListAdapter(context, dateTimeSlotRecords)
        expandableListView.setAdapter(expandableListAdapter)

    }



}