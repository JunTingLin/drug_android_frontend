package com.junting.drug_android_frontend.ui.drugRecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.junting.drug_android_frontend.databinding.FragmentDrugRecordsBinding

class DrugRecordsFragment : Fragment() {

    private var _binding: FragmentDrugRecordsBinding? = null
    private var drugRecordsPagerAdapter: DrugRecordsPagerAdapter? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDrugRecordsBinding.inflate(inflater, container, false)
        setViewPager()
        return binding.root
    }

    private fun setViewPager() {
        drugRecordsPagerAdapter = DrugRecordsPagerAdapter(this.requireContext())
        binding.drugRecordsViewPager.adapter = drugRecordsPagerAdapter
        binding.drugRecordsViewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.drugRecordsTabLayout))
        binding.drugRecordsTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) { updatePosition(tab) }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) { updatePosition(tab) }
            fun updatePosition(tab: TabLayout.Tab) {
                binding.drugRecordsViewPager.currentItem = tab.position
                if (tab.position == 1) {
                    drugRecordsPagerAdapter!!.refreshHospitalPage()
                }
                if (tab.position == 2) {
                    drugRecordsPagerAdapter!!.refreshDepartmentPage()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}