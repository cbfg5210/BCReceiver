package cbfg.bcreceiver

import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTime.setOnClickListener(this)
        btnBattery.setOnClickListener(this)
        btnHome.setOnClickListener(this)
        btnScreen.setOnClickListener(this)
        btnNet.setOnClickListener(this)
        btnPkg.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        v.isEnabled = false

        when (v.id) {
            R.id.btnTime -> {
                BCReceiver()
                    .withFilter { intentFilter ->
                        intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
                        intentFilter.addAction(Intent.ACTION_TIME_TICK)
                    }
                    .setCallback { _, _ -> Log.e("***", "${System.currentTimeMillis()}") }
                    .bind(this, lifecycle)
            }

            R.id.btnBattery -> {
                BCReceiver()
                    .withFilter { intentFilter ->
                        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
                        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
                        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
                        intentFilter.addAction(Intent.ACTION_BATTERY_LOW)
                        //由低电状态恢复电量
                        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY)
                    }
                    .setCallback { context, intent -> Log.e("***", "action = ${intent.action}") }
                    .bind(this, lifecycle)
            }

            R.id.btnHome -> {
                BCReceiver()
                    .withFilter { intentFilter ->
                        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                    }
                    .setCallback { _, intent -> Log.e("***", "action = ${intent.action}") }
                    .bind(this, lifecycle)
            }

            R.id.btnScreen -> {
                BCReceiver()
                    .withFilter { intentFilter ->
                        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
                        //息屏(锁屏)
                        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
                        //屏幕解锁
                        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
                    }
                    .setCallback { _, intent -> Log.e("***", "action = ${intent.action}") }
                    .bind(this, lifecycle)
            }

            R.id.btnNet -> {
                BCReceiver()
                    .withFilter { intentFilter ->
                        //监听网络连接,包括 wifi 和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
                        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                        //用于判断是否连接到了有效 wifi (不能用于判断是否能够连接互联网)
                        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                        //wifi 打开或关闭的状态
                        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
                        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
                        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                    }
                    .setCallback { _, intent -> Log.e("***", "intent = $intent") }
                    .bind(this, lifecycle)
            }

            R.id.btnPkg -> {
                BCReceiver()
                    .withFilter { intentFilter ->
                        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
                        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
                        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
                        intentFilter.addDataScheme("package")
                    }
                    .setCallback { _, intent ->
                        Log.e("***", "intent = $intent,dataString = ${intent.dataString}")
                    }
                    .bind(this, lifecycle)
            }
        }
    }
}