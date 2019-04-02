package com.ashwin.android.sample.diygeogencedemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.ashwin.android.library.diygeofence.DiyGeofenceManager
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "debug-log"
        const val PREFS_FILENAME = "MY_PREFS"
        const val LOCATION_PERMISSIONS_REQUEST_CODE = 1025
        const val NEVER_ASK_AGAIN = "never_ask_again"
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = applicationContext.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        add_button.setOnClickListener {
            addGeofence()
        }

        remove_button.setOnClickListener {
            removeGeofence()
        }
    }

    private fun loadGeofences() {
        val geofencesList = DiyGeofenceManager.getAddedGeofences(this)
        if (geofencesList.size > 0) {
            val strBuilder = StringBuilder()
            for (geofence in geofencesList) {
                strBuilder.append(geofence.toString()).append("\n\n")
            }
            geofences_textview.text = strBuilder.toString()
        } else {
            geofences_textview.text = "No geofences added"
        }
    }

    private fun addGeofence() {
        val id = id_edittext.text.toString().trim()
        if (id.isBlank()) {
            Toast.makeText(this, "Empty Geofence ID", Toast.LENGTH_LONG).show()
            return
        }

        val strLat = lat_edittext.text.toString().trim()
        val strLng = lng_edittext.text.toString().trim()
        try {
            val lat = strLat.toDouble()
            val lng = strLng.toDouble()
            DiyGeofenceManager.addGeofence(this, id, lat, lng, 500.0)

            loadGeofences()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "could not add geofence", e)
            Toast.makeText(this, "Invalid location", Toast.LENGTH_LONG).show()
        }
    }

    private fun removeGeofence() {
        val id = id_edittext.text.toString().trim()
        if (id.isBlank()) {
            Toast.makeText(this, "Empty Geofence ID", Toast.LENGTH_LONG).show()
            return
        }

        DiyGeofenceManager.removeGeofence(this, id)
        loadGeofences()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission_button.isEnabled = true
            permission_button.text = "REQUEST"
            permission_button.setOnClickListener {
                if (sharedPreferences.getBoolean(NEVER_ASK_AGAIN, false)) {
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)))
                } else {
                    requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        } else {
            sharedPreferences.edit().putBoolean(NEVER_ASK_AGAIN, false).apply()
            permission_button.text = "GRANTED"
            permission_button.isEnabled = false
        }

        loadGeofences()
    }

    private fun requestPermission(permission: String) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSIONS_REQUEST_CODE) {
            return
        }

        sharedPreferences.edit().putBoolean(NEVER_ASK_AGAIN, false).apply()
        if (grantResults.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "onRequestPermissionsResult() > User interaction was cancelled.")
            }
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
            DiyGeofenceManager.updateLocation(applicationContext)
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                sharedPreferences.edit().putBoolean(NEVER_ASK_AGAIN, true).apply()
            }
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
        }
    }
}
