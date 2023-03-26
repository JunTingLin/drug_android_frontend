package com.junting.drug_android_frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.junting.drug_android_frontend.model.InteractingDrugs
import com.junting.drug_android_frontend.Util.OkHttpUtil
import com.junting.drug_android_frontend.databinding.ActivityDrugsInteractionBinding
import com.junting.drug_android_frontend.Util.OkHttpUtil.Companion.mOkHttpUtil
import okhttp3.Response

class DrugsInteractionActivity : AppCompatActivity() {

    private lateinit var viewAdapter: DrugsInteractionAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityDrugsInteractionBinding
    val InteractionDataUrl = "https://my-json-server.typicode.com/JunTingLin/drug-json-api-server/drugsInteraction"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrugsInteractionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        getDrugsInteractionData()

    }

    private fun initView(){
        // 定義 LayoutManager 為 LinearLayoutManager
        viewManager = LinearLayoutManager(this)

        viewAdapter = DrugsInteractionAdapter()

        // 定義從佈局當中，拿到 recycler_view 元件，
        binding.recyclerView.apply {
            // 透過 kotlin 的 apply 語法糖，設定 LayoutManager 和 Adapter
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun getDrugsInteractionData() {
        //顯示忙碌圈圈
        binding.progressBar.visibility = View.VISIBLE

        mOkHttpUtil.getAsync(InteractionDataUrl, object : OkHttpUtil.ICallback {
            override fun onResponse(response: Response) {
                val interactingDrugsData = response.body?.string()

                val drugsInteractionInfo = Gson().fromJson(interactingDrugsData, InteractingDrugs::class.java)

                runOnUiThread {
                    //將下載的交互作用資料，指定給 DrugsInteractionAdapter
                    viewAdapter.DrugsInteractionList = drugsInteractionInfo.interactionDrugs

                    //關閉忙碌圈圈
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(e: okio.IOException) {
                Log.d("DIA", "onFailure: $e")

                //關閉忙碌圈圈
                binding.progressBar.visibility = View.GONE
            }
        })
    }
}