package com.android_hw.hw4

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.android_hw.hw4.fragments.ChannelSelectListener
import com.android_hw.hw4.fragments.HomeFragment
import com.android_hw.hw4.fragments.HomeListener


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



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        mApp = application as MyApp

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

