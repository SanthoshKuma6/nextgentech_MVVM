package com.nextgenapp.nextgentech.ui.main.bottomnav

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nextgenapp.nextgentech.R
import com.nextgenapp.nextgentech.data.model.UserDetail
import com.nextgenapp.nextgentech.data.pref.PrefSessionManager
import com.nextgenapp.nextgentech.databinding.ActivityBottomNavBinding
import com.nextgenapp.nextgentech.ui.base.BaseActivity
import com.nextgenapp.nextgentech.utils.Session
import kotlinx.coroutines.GlobalScope

class BottomNavActivity : BaseActivity() {

    //view binding initialization
    private lateinit var activityBottomNavBinding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBottomNavBinding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(activityBottomNavBinding.root)

        //initialize the bottom navigation
        val navView: BottomNavigationView = activityBottomNavBinding.navView

        //attaching the navigation controller to the bottom navigation
        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_nav)
        navView.setupWithNavController(navController)

        prefData()
    }

    /**
     * During the successful login we store the user credentials in the shared preference
     * Here we are retrieving the stored credentials and assigning it to the variable and
     * can be accessed throughout the application from the Session class
     */
    private fun prefData(){
        // get the user details
        val getLoginDetails = PrefSessionManager(this).getUserDetails()
        // get the user name
        val userName = getLoginDetails[PrefSessionManager.USER_NAME].toString()
        // get the user token
        val userToken = getLoginDetails[PrefSessionManager.USER_TOKEN].toString()

        //store the values from shared preference to the Session variable
        Session.userDetail = UserDetail(name = userName, token = userToken)
    }
}