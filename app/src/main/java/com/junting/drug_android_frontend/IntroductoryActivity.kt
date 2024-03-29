package com.junting.drug_android_frontend

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.junting.drug_android_frontend.libs.SharedPreferencesManager
import org.json.JSONObject
import java.util.*
import kotlin.math.abs


class IntroductoryActivity : AppCompatActivity() {
    private lateinit var mViewPage: ViewPager
    private lateinit var nextBtn: Button
    private lateinit var googleSignInBtn: Button
    private lateinit var introLoginCorrectBtn: Button
    private lateinit var btScanBtn: Button
    private lateinit var viewList: ArrayList<View>
    private lateinit var dots: Array<TextView>
    private lateinit var mDotLayout: LinearLayout
    private val MIN_SWIPE_DISTANCE = 150 // 右滑/左滑最小滑動距離
    private val MAX_SWIPE_DISTANCE = 300 // 右滑/左滑最大垂直滑動距離
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f
    //  google one tap
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest

    private val sharedPreferencesManager by lazy {
        SharedPreferencesManager(this)
    }


    private val listItems = ArrayList<BluetoothDevice>()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var permissionMissing = true
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val REQUEST_BLUETOOTH_CONNECT_PERMISSION = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introductory)
        supportActionBar?.hide()

        initView()
        initAdapter()

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // init section
        googleSignInBtn = viewList[1].findViewById(R.id.intro_google_Btn)
        introLoginCorrectBtn = viewList[1].findViewById(R.id.intro_login_correct_Btn)
        btScanBtn = viewList[2].findViewById(R.id.intro_search_Btn)
        nextBtn = viewList[3].findViewById(R.id.intro_next_Btn)


        //      google one tap start
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true) // Your server's client ID, not your Android client ID.
                    .setServerClientId(BuildConfig.Web_Client_Id) // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
        val activityResultLauncher = registerForActivityResult<IntentSenderRequest, ActivityResult>(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        sharedPreferencesManager.saveGoogleIdToken(idToken)
                        Log.d("TAG", "onCreate: $idToken")
                        val userEmail = credential.id
                        sharedPreferencesManager.saveUserEmail(userEmail)
                        Log.d("TAG", "We have : $userEmail")

                        // Retrieve the profile picture URL from the Google ID token
                        val idTokenComponents = idToken.split(".")
                        if (idTokenComponents.size == 3) {
                            val idTokenPayload =
                                String(Base64.decode(idTokenComponents[1], Base64.DEFAULT))
                            val jsonPayload = JSONObject(idTokenPayload)
                            val userPictureUrl = jsonPayload.optString("picture")
                            val userName = jsonPayload.optString("name")
                            sharedPreferencesManager.savePictureUrl(userPictureUrl)
                            Log.d("TAG", "We have : $userPictureUrl")
                            sharedPreferencesManager.saveUserName(userName)
                            Log.d("TAG", "We have : $userName")
                        }
                        introLoginCorrectBtn.visibility = View.VISIBLE
                        googleSignInBtn.visibility = View.GONE

                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
        //      google one tap end

//      按鈕設定

        //      使用google 登入

        googleSignInBtn.setOnClickListener {
            if (mViewPage.currentItem == 1) { // 當使用者處於 intro_page1的時候
                oneTapClient.beginSignIn(signUpRequest)
                    .addOnSuccessListener(
                        this
                    ) { result ->
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        activityResultLauncher.launch(intentSenderRequest)
                    }
                    .addOnFailureListener(
                        this
                    ) { e -> // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d("TAG", e.localizedMessage)
                    }
            }
        }


        // bluetooth scan
        btScanBtn.setOnClickListener{
            refresh()
        }



        //      開始使用程式
        nextBtn.setOnClickListener {
            val idToken = sharedPreferencesManager.getGoogleIdToken()
            if(idToken != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "請先登入", Toast.LENGTH_SHORT).show()
            }

        }

        mDotLayout = findViewById<View>(R.id.indicator_layout) as LinearLayout
        mViewPage.addOnPageChangeListener(viewListener)
        setUpindicator(0)

    }



    private fun initAdapter() {
        val adapter = IntroductoryAdapter(viewList)
        mViewPage.adapter = adapter
    }

    // Initialize ViewPager and the four introductory pages
    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        mViewPage = findViewById(R.id.introductory_viewPager)
        viewList = ArrayList()
        viewList.add(getView(R.layout.intro_page0))
        viewList.add(getView(R.layout.intro_page1))
        viewList.add(getView(R.layout.intro_page2))
        viewList.add(getView(R.layout.intro_page3))

        // 設置滑動手勢
        mViewPage.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    y1 = event.y
                    false
                }
                MotionEvent.ACTION_UP -> {
                    x2 = event.x
                    y2 = event.y
                    val deltaX = x2 - x1
                    val deltaY = y2 - y1
                    if (abs(deltaX) > MIN_SWIPE_DISTANCE && abs(deltaY) < MAX_SWIPE_DISTANCE) {
                        if (deltaX > 0 && mViewPage.currentItem > 0) {
                            // 由右向左滑回上一頁
                            mViewPage.currentItem -= 1
                        } else if (deltaX < 0 && mViewPage.currentItem < viewList.size - 1) {
                            // 由左向右滑進入下一頁
                            mViewPage.currentItem += 1
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun getView(resId: Int): View {
        return LayoutInflater.from(this).inflate(resId, null)
    }

    fun setUpindicator(position: Int) {
        dots = Array<TextView>(4) { TextView(this) }
        mDotLayout.removeAllViews()

        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i].text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            dots[i].textSize = 35f
            dots[i].setTextColor(getColor(R.color.inactive))
            mDotLayout.addView(dots[i])
        }

        dots[position].setTextColor(getColor(R.color.active))
    }



//  ViewPager 頁面改變監聽器
    var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

//      當頁面選中時，調用 setUpindicator(position) 方法來更新指示器的狀態
        override fun onPageSelected(position: Int) {
            setUpindicator(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    @SuppressLint("MissingPermission")
    fun refresh() {
        listItems.clear()
        if (bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val permissionMissing = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            }
            if (!permissionMissing) {
                for (device in bluetoothAdapter!!.getBondedDevices()) if (device.type != BluetoothDevice.DEVICE_TYPE_LE) listItems.add(
                    device
                )

                var found: Boolean = false

                for (device: BluetoothDevice in listItems){
                    if (device.getName() == "我的智慧藥盒") {
                        val btDeviceName: TextView = findViewById<TextView>(R.id.text1)
                        val btDeviceAddress: TextView = findViewById<TextView>(R.id.text2)
                        btDeviceName.text = device.getName()
                        btDeviceAddress.text = device.getAddress()
                        found = true
                        btDeviceName.setOnClickListener {
                            val deviceToPair = listItems.find { it.address == "78:21:84:8C:A6:92" }
                            deviceToPair?.let {
                                if (it.createBond()) {
                                    showMessageDialog("Device paired")
                                } else {
                                    showMessageDialog("Device not paired")
                                }
                            }
                        }
                    }
                }
                if (!found){
                    showMessageDialog("Could not find the device")
                }
            }
        }
        if (bluetoothAdapter == null) {
            showMessageDialog("no bluetooth adapter")
        }
        else if (!bluetoothAdapter!!.isEnabled()) {
            showMessageDialog("bluetooth is disabled")
        }
        else if (permissionMissing) {
            showMessageDialog("need bluetooth permission")
            checkBluetoothConnectPermission()
        }
    }
    private fun showMessageDialog(text: String) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Message Dialog")
            setMessage(text)
            setPositiveButton("OK") { dialog, which ->
                // Code to be executed when the 'OK' button is clicked
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun checkBluetoothConnectPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it at runtime
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_BLUETOOTH_CONNECT_PERMISSION
            )
        } else {
            // Permission already granted, perform your Bluetooth operation that requires BLUETOOTH_CONNECT permission here
        }
        permissionMissing = false
    }

}