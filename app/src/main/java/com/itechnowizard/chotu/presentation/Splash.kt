package com.itechnowizard.chotu.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivitySplashBinding
import com.itechnowizard.chotu.presentation.auth.LoginActivity
import com.itechnowizard.chotu.presentation.dashboard.DashboardActivity

class Splash : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    private val SPLASH_SCREEN_TIME_OUT = 2000L // 2 seconds
    private val VIDEO_SCREEN_TIME_OUT = 8000L // 8 seconds


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        if(FirebaseAuth.getInstance().currentUser !=null){
            startActivity(Intent(this,DashboardActivity::class.java))
            finish()
        }else{

            setContentView(binding.root)

            binding.splashVideo.setVideoPath("android.resource://" + packageName + "/" + R.raw.splash_video)
//        startActivity()
//         Use a handler to delay the opening of the video
            Handler(Looper.getMainLooper()).postDelayed({
                binding.splashImage.visibility = View.GONE
                binding.splashVideo.visibility = View.VISIBLE
                binding.splashVideo.start()

                binding.splashVideo.setOnCompletionListener {
                    startActivity()
                }
            }, SPLASH_SCREEN_TIME_OUT)
        }



    }

    fun startActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}