package cbfg.bcreceiver

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wifi_list.*
import pub.devrel.easypermissions.EasyPermissions

class WifiListActivity : AppCompatActivity(R.layout.activity_wifi_list),
    View.OnClickListener,
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    companion object {
        private const val TAG = "WifiListActivity"
        private val PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager

    private val hasPermissions: Boolean
        get() = EasyPermissions.hasPermissions(this, *PERMISSIONS)

    private val isLocationEnabled: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnGetWifiList.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnGetWifiList -> {
                if (checkLocationEnabled()) {
                    val results = wifiManager.scanResults
                    Log.e(TAG, "results =$results, size = ${results.size}")
                } else {
                    val results = wifiManager.scanResults
                    Log.e(TAG, "results2 =$results, size = ${results.size}")
                }
            }
        }
    }

    private fun checkLocationEnabled(): Boolean {
        if (!hasPermissions) {
            EasyPermissions.requestPermissions(
                this,
                "获取 wifi 列表需要使用位置权限",
                1112,
                *PERMISSIONS
            )
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!isLocationEnabled) {
                AlertDialog.Builder(this)
                    .setMessage("获取周边 wifi 信息需要开启定位服务")
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("去开启") { dialog, _ ->
                        dialog.dismiss()
                        openGpsSettings()
                    }
                    .create()
                    .show()
                return false
            }
        }
        return true
    }

    /**
     *  打开Gps设置界面
     */
    private fun openGpsSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (hasPermissions) {
            checkLocationEnabled()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    override fun onRationaleDenied(requestCode: Int) {
        Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}