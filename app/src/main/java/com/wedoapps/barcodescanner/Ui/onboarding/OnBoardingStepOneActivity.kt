package com.wedoapps.barcodescanner.Ui.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.wedoapps.barcodescanner.Ui.Choice.HomeActivity
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.PreferenceHelper
import com.wedoapps.barcodescanner.databinding.ActivityBusinessDetailsScreenOneBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnBoardingStepOneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusinessDetailsScreenOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                lifecycleScope.launch {
                    delay(1000)
                }
                false
            }
        }
        (application as BarcodeApplication).pref
        super.onCreate(savedInstanceState)
        if (PreferenceHelper.isOnBoardingCompleted()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            binding = ActivityBusinessDetailsScreenOneBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnScreenOneDone.setOnClickListener {
                startActivity(Intent(this, OnBoardingStepTwoActivity::class.java))
                finish()
            }
        }

    }
}