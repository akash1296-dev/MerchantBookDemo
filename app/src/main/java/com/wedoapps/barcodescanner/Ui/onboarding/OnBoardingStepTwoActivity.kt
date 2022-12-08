package com.wedoapps.barcodescanner.Ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wedoapps.barcodescanner.Ui.Choice.HomeActivity
import com.wedoapps.barcodescanner.Utils.PreferenceHelper
import com.wedoapps.barcodescanner.databinding.ActivityBusinessDetailsScreenTwoBinding

class OnBoardingStepTwoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusinessDetailsScreenTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessDetailsScreenTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateCompany.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            PreferenceHelper.setOnBoardingCompleted()
            finish()
        }
    }

}