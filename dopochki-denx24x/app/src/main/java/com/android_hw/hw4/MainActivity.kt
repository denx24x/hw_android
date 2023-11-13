package com.android_hw.hw4

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.android_hw.hw4.fragments.ChannelSelectListener
import com.android_hw.hw4.fragments.HomeFragment
import com.android_hw.hw4.fragments.HomeListener
import com.google.android.material.button.MaterialButton
import java.io.File


class MainActivity : AppCompatActivity(), ChannelSelectListener {
    private val TAG = "MainActivity"
    private lateinit var navController : NavController
    private lateinit var mApp : MyApp

    override fun onChannelSelected(selected: String){
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mApp.switchChannel(selected)
            return
        }
        val fragment: HomeFragment? = supportFragmentManager
            .findFragmentByTag("messageList") as HomeFragment?
        if (fragment != null && fragment.isVisible) {
            Log.i(TAG, "already exists")
            //fragment.setSelectedItem(selectedItem)
        } else {
            val nextFrag = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, nextFrag, "messageList")
                .addToBackStack(null)
                .commit()
            Log.i(TAG, "registered port")
            mApp.registerChat(nextFrag)
        }
        mApp.switchChannel(selected)

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            42 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mApp.cameraEnabled = true
                } else {
                    Log.i(TAG, "permission not granted")
                }
                return
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        mApp = application as MyApp
        mApp.cameraCaller = this

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                42)
        } else {
            mApp.cameraEnabled = true
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "registered land")
            mApp.registerChat(supportFragmentManager.findFragmentById(R.id.messageListLand) as HomeFragment)
        }else{
            val fragment: HomeFragment? = supportFragmentManager
                .findFragmentByTag("messageList") as HomeFragment?
            if(fragment != null){
                Log.i(TAG, "registered port")
                mApp.registerChat(fragment)
            }else{
                Log.i(TAG, "registered null")
                mApp.registerChat(null)
            }
        }

        //if (findViewById<BottomNavigationView>(R.id.bottom_navigation) != null) {
        //    bottomNavigation = findViewById(R.id.bottom_navigation)
        //    setupBottomNavigation()
        //}
    }

}

