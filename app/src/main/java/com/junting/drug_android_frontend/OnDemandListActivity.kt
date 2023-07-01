package com.junting.drug_android_frontend

import android.R
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.junting.drug_android_frontend.databinding.ActivityOnDemandListBinding


class OnDemandListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnDemandListBinding
    private var viewModel: OnDemandViewModel = OnDemandViewModel()
    private var viewAdapter: OnDemandRecyclerViewAdapter = OnDemandRecyclerViewAdapter(this,viewModel)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnDemandListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViewModel()
        initRecyclerView()

    }

    private fun initViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchDrugRecordByOnDemand(true)
        viewModel.onDemandDrugRecors.observe(this, Observer {
            Log.d("Observe onDemandDrugRecors", "record: ${it.toString()}")
            viewAdapter!!.notifyDataSetChanged()
            binding.progressBar.visibility = View.GONE
        })
    }
    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}