package com.junting.drug_android_frontend.ui.drugRecords

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.junting.drug_android_frontend.InputManuallyActivity
import com.junting.drug_android_frontend.R
import com.junting.drug_android_frontend.TakePhotoActivity


class DrugRecordsAllPage(context: Context, container: ViewGroup) {

    val view: View
    private val context: Context
    private val container: ViewGroup

    private val progressBar: ProgressBar
    private val recyclerView: RecyclerView
    private var recyclerAdapter: DrugsRecordViewAdapter
    private var fab: ExtendedFloatingActionButton

    private val viewModel: DrugRecordsViewModel



    init {
        this.context = context
        this.container = container
        this.view = LayoutInflater.from(context).inflate(R.layout.drug_records_all_tab, container, false)
        this.progressBar = view.findViewById(R.id.progressBar)
        this.recyclerView = view.findViewById(R.id.recycler_view)
        this.fab=view.findViewById(R.id.fab)
        this.viewModel = DrugRecordsViewModel()
        this.recyclerAdapter = DrugsRecordViewAdapter(context, viewModel)
        this.initAdapter()
        this.initProgressBar()
        this.initFab()
        this.initRecyclerView()
    }

    private fun initAdapter() {
        this.viewModel.fetchRecords()
        this.viewModel.records.observe(context as AppCompatActivity, Observer {
            recyclerAdapter!!.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        })
    }

    private fun initProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerAdapter
        }
    }
    private fun initFab(){
        fab.setOnClickListener(View.OnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
                .setTitle("詢問")
                .setMessage("請問您要使用何種方式輸入藥品?")
                .setPositiveButton("手動") { dialogInterface, i ->
                    startActivity(context, Intent(context, InputManuallyActivity::class.java), null)
                    dialogInterface.dismiss()
                }
                .setNegativeButton("拍攝") { dialogInterface, i ->
                    startActivity(context, Intent(context, TakePhotoActivity::class.java), null)
                }
            builder.create()
            builder.show()
        })
    }
}