package com.saqcess.qicpic.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.saqcess.qicpic.R
import com.saqcess.qicpic.app.utils.ConnectivityReceiver
import com.saqcess.qicpic.app.utils.Contrants
import com.saqcess.qicpic.app.utils.MyApplication
import com.saqcess.qicpic.app.utils.SessionManager
import com.saqcess.qicpic.appupdate.AppUpdateChecker
import com.saqcess.qicpic.appupdate.AppVersionModel
import com.saqcess.qicpic.databinding.ActivityHomeBinding
import com.saqcess.qicpic.databinding.ToolbarViewBinding
import com.saqcess.qicpic.view.dialog.DialogAppUpdater
import com.tbruyelle.rxpermissions2.RxPermissions


@Suppress("DEPRECATION")
class HomeActivity : BaseActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var toolbarViewBinding: ToolbarViewBinding
    private lateinit var session: SessionManager


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val toolbar = binding.includeToolbar.llHomeToolbar
        //setting toolbar
        setSupportActionBar(toolbar)
        //home navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        session = SessionManager(applicationContext)
        //session.checkLogin();
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_home,
                        R.id.navigation_video,
                        R.id.navigation_posts,
                        R.id.navigation_like,
                        R.id.navigation_profile
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (session.userDetails[SessionManager.KEY_PROFILE_PICTURE] != null) {
            Glide.with(getApplicationContext()).asBitmap()
                    .load(session.userDetails[SessionManager.KEY_PROFILE_PICTURE]!!)
                    .apply(RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.qicpiclogo))
                    .into(object : CustomTarget<Bitmap>() {

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val profileImage = BitmapDrawable(getResources(), resource)

                            binding.navView.itemIconTintList = null
                            val menu = binding.navView.menu
                            val menuItem = menu.findItem(R.id.navigation_profile)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                menuItem?.iconTintList = null
                                menuItem?.iconTintMode = null
                            }
                            menuItem?.icon = profileImage
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })


        }

        // Manually checking internet connection
        checkConnection()
        checkUpdate()

        if (!session.isLoggedIn) {
            logoutUser()
        }

        val rxPermissions = RxPermissions(this)
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe({ granted ->
                    if (granted) {
                        // All requested permissions are granted
                    } else {
                        // At least one permission is denied
                    }
                })
        // checkPermission()
    }

    // Method to manually check connection status
    private fun checkConnection() {
        val isConnected = ConnectivityReceiver.isConnected()
        showSnack(isConnected)
    }

    var dialogAppUpdater: DialogAppUpdater? = null
    var appVersionModel: AppVersionModel? = null
    private fun checkUpdate() {


        AppUpdateChecker(this, object : AppUpdateChecker.AppUpdateAvailableListener {
            override fun appUpdateAvailable(appUpdatemodal: LiveData<AppVersionModel?>) {
                appUpdatemodal.observe(this@HomeActivity, Observer { appVersionModel ->
                    if (appVersionModel != null) {
                        this@HomeActivity.appVersionModel = appVersionModel
                        if (appVersionModel.getCode() === 200) {
                            if (isFinishing) return@Observer
                            callupdatedialog()
                        }
                    }
                })
            }
        }).checkForUpdate()
    }

    private fun callupdatedialog() {
        try {
            if (dialogAppUpdater != null && dialogAppUpdater!!.isShowing) {
                dialogAppUpdater!!.dismiss()
            }
            dialogAppUpdater = DialogAppUpdater(this@HomeActivity, appVersionModel)
            dialogAppUpdater!!.show()
        } catch (e: Exception) {
            Log.d("HomeActivity", "--exception-" + e.message)
        }
    }

    private fun logoutUser() {
        session.setLogin(false)
        // Launching the login activity
        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionCamer = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Contrants.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION)
        } else if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Contrants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)
        } else if (permissionCamer != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Contrants.REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onResume() {
        super.onResume()

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this)

        if (appVersionModel != null) {
            if (appVersionModel!!.code === 200) {
                if (isFinishing) return
                callupdatedialog()
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private fun showSnack(isConnected: Boolean) {
        val message: String
        val color: Int
        if (isConnected) {
            //  message = "Good! Connected to Internet"
            // color = Color.WHITE
        } else {
            message = "Sorry! Not connected to internet"
            color = Color.RED
            showSnackbar(binding.container, message, Snackbar.LENGTH_SHORT)
        }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}