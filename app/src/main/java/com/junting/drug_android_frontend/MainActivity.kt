package com.junting.drug_android_frontend

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.junting.drug_android_frontend.databinding.ActivityMainBinding
import com.junting.drug_android_frontend.databinding.FontSizeDialogLayoutBinding
import com.junting.drug_android_frontend.libs.FontSizeManager
import com.junting.drug_android_frontend.libs.LanguageUtil
import com.junting.drug_android_frontend.libs.SharedPreferencesManager
import com.junting.drug_android_frontend.ui.libs.updater.UpdateUIHelper
import com.squareup.picasso.Picasso
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navigationView: NavigationView
    private lateinit var headerTextView: TextView
    private lateinit var setTodayReminderBadgeReceiver: BroadcastReceiver
    private val sharedPreferencesManager by lazy {
        SharedPreferencesManager(this)
    }

    //    var receiveIntent: Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idToken = sharedPreferencesManager!!.getGoogleIdToken()

        //這段暫時性關閉-俊霆，之後要打開
//        if (idToken == null) {
//            val intent = Intent(this, IntroductoryActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        val navView: BottomNavigationView = binding.navView
        // 設定顯示小紅點圖標
        navView.getOrCreateBadge(R.id.navigation_todayReminder).apply {
            number = 0
            isVisible = true
        }

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_todayReminder, R.id.navigation_drugRecords,
                R.id.navigation_takeRecords, R.id.navigation_pillBoxManagement
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // customize the action bar, whatever appearance you want it to be in action_bar_view
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar_view)

        // refresh the actionbar menu when change fragment
        navController.addOnDestinationChangedListener { _: NavController, d: NavDestination, _: Bundle? ->
            supportActionBar!!.customView.findViewById<TextView>(R.id.action_bar_title).text =
                d.label
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_outline_menu_24)
        }

        initLanguageMenuItemTitle()

        //          get user information
        // 获取 NavigationView
        val navigationView: NavigationView = findViewById(R.id.drawer_nav)

        // 获取头部视图
        val headerView = navigationView.getHeaderView(0)

        // 获取视图元素的引用
        val imageView: ImageView = headerView.findViewById(R.id.signInImage)
        val signInEmailTextView: TextView = headerView.findViewById(R.id.tvEmail_HeaderNavView)
        val nameView: TextView = headerView.findViewById(R.id.tvName_HeaderNavView)
        val receivedEmail = sharedPreferencesManager!!.getUserEmail()
        val receivedUrl = sharedPreferencesManager!!.getPictureUrl()
        val receivedName = sharedPreferencesManager!!.getUserName()
        signInEmailTextView.text = receivedEmail
        Picasso.get().load(receivedUrl).into(imageView)
        nameView.text = receivedName


        binding.drawerNav.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_drug_interaction_lookup -> {
                    Log.d("drawerNav.", "nav_tools")
                    // 在這裡處理點擊事件
                    binding.drawer.closeDrawer(GravityCompat.START)
                    val intent = Intent(this, MedicineSearchActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_language -> {
                    Log.d("drawerNav.", "nav_language")
                    // 在這裡處理點擊事件
                    //switchLanguage
                    //修改配置
                    LanguageUtil.settingLanguage(this, LanguageUtil.getInstance())
                    binding.drawer.closeDrawer(GravityCompat.START)
                    //activity銷毀重建
                    this.recreate()

                    true
                }

                R.id.nav_fontSize -> {
                    Log.d("drawerNav.", "nav_fontSize")
                    // 在這裡處理點擊事件
                    initFontSizeDialog()
                    binding.drawer.closeDrawer(GravityCompat.START)
                    true

                }

                R.id.nav_logout -> {
                    Log.d("drawerNav.", "nav_logout")
                    // 在這裡處理點擊事件
                    showLogoutConfirmationDialog()
                    binding.drawer.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }

        val fragmentName = intent.getStringExtra("fragmentName")
        if (fragmentName == "TodayReminderFragment") {
            navController.popBackStack()
            navController.navigate(R.id.navigation_todayReminder)
        } else if (fragmentName == "DrugRecordsFragment") {
            navController.popBackStack()
            navController.navigate(R.id.navigation_drugRecords)
        } else if (fragmentName == "TakeRecordsFragment") {
            navController.popBackStack()
            navController.navigate(R.id.navigation_takeRecords)
        } else if (fragmentName == "PillBoxManagementFragment") {
            navController.popBackStack()
            navController.navigate(R.id.navigation_pillBoxManagement)
        }
        setupUpdateUIListener()

        // sending FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", "Token: $token")
                sendTokenToServer(token)
            } else {
                Log.e("FCM Token", "Error getting token: ${task.exception}")
            }
        }

        //check push notification permission
        askNotificationPermission()
    }

    // out of onCreate
    private fun initFontSizeDialog() {
        val binding = FontSizeDialogLayoutBinding.inflate(layoutInflater)
        val fontSizeSeekBar = binding.seekbar
        fontSizeSeekBar.progress = FontSizeManager.getCurrentFontScaleIndex()

        // 設置滑桿的監聽器
        fontSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // 根據進度值調整字體大小
                Log.d("fontSizeSeekBar", "fontScale: $FontSizeManager.getCurrentFontScaleIndex()")
                FontSizeManager.setCurrentFontScaleIndex(progress)
                setAppFontSize(FontSizeManager.getCurrentFontScale())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // 滑桿觸摸開始時的處理
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // 滑桿觸摸結束時的處理
            }
        })

        // 建立對話框
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.adjust_text_size))
            .setView(binding.root)
            .setPositiveButton(resources.getString(R.string.confirm)) { dialog, _ ->
                // 按下確定按鈕的處理
                recreate() // 重新啟動 Activity 以應用新的字體大小設定
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                // 按下取消按鈕的處理
                dialog.dismiss()
            }
            .show()
    }

    fun initLanguageMenuItemTitle() {
        val currentLocale: Locale = resources.configuration.locales.get(0)
        val language: String = currentLocale.language
        if (language == "en") {
            binding.drawerNav.menu.findItem(R.id.nav_language).title = "中文"
        } else if (language == "zh") {
            binding.drawerNav.menu.findItem(R.id.nav_language).title = "English"
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("TAG", "onRestoreInstanceState");
        activityReset();
    }

    private fun activityReset() {
        //同一个对象在同一个线程中获取的数据是一样的，因为每个线程都维护了一个ThreadLocalMap对象，key值是ThreadLocal
        val threadLocal: ThreadLocal<String> = LanguageUtil.getInstance() as ThreadLocal<String>
        val language: String? = threadLocal.get()
        Log.d("language: ", language.toString())
        if (language == "English") {
            binding.drawerNav.menu.findItem(R.id.nav_language).title = "中文"
        } else if (language == "Chinese") {
            binding.drawerNav.menu.findItem(R.id.nav_language).title = "English"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawer.openDrawer(GravityCompat.START)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setAppFontSize(fontScale: Float) {
        val configuration = resources.configuration
        configuration.fontScale = fontScale
        val metrics = resources.displayMetrics
        resources.updateConfiguration(configuration, metrics)
    }

    fun setTodayReminderBadge(number: Int) {
        val navView: BottomNavigationView = binding.navView
        val badge = navView.getOrCreateBadge(R.id.navigation_todayReminder)
        badge.number = number
        badge.isVisible = true
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.sign_out_confirmation))
            .setMessage(resources.getString(R.string.sign_out_text))
            .setPositiveButton(resources.getString(R.string.confirm)) { dialog, _ ->
                // 點擊確定按鈕時執行登出操作
                performLogout()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                // 點擊取消按鈕時關閉對話框
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        sharedPreferencesManager.clearGoogleIdToken()
        sharedPreferencesManager.clearPictureUrl()
        sharedPreferencesManager.clearUserEmail()
        sharedPreferencesManager.clearUserName()
        startActivity(Intent(this@MainActivity, IntroductoryActivity::class.java))
        finish()
    }

    private fun setupUpdateUIListener() {
        setTodayReminderBadgeReceiver =
            UpdateUIHelper.listenInt(baseContext, "setTodayReminderBadge") { t ->
            setTodayReminderBadge(t)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UpdateUIHelper.unListen(baseContext, setTodayReminderBadgeReceiver)
    }

    fun sendTokenToServer(token: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://bosh.pythonanywhere.com/addToken/${token}")

            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                Log.d("FCM token", "Response Code: $responseCode")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            Toast.makeText(this, "你不能收到吃藥通知", Toast.LENGTH_SHORT).show()
        }
    }
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
