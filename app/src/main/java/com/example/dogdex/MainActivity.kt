package com.example.dogdex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dogdex.Settings.SettingActivity
import com.example.dogdex.api.ApiServiceInterceptor
import com.example.dogdex.auth.LoginActivity
import com.example.dogdex.databinding.ActivityMainBinding
import com.example.dogdex.doglist.DogListActivity
import com.example.dogdex.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateSession()
        initView()
    }

    private fun initView() {
        with(binding) {
            settingsFab.setOnClickListener {
                openSettingActivity()
            }
            dogListFab.setOnClickListener {
                openDogListActivity()
            }
        }
    }

    private fun validateSession() {
        val user = User.getLoggedInUser(this)

        if (user == null) {
            openLogin()
            return
        } else {
            ApiServiceInterceptor.setSessionToken(user.authenticationToken)
        }
    }

    private fun openDogListActivity() {
        startActivity(Intent(this@MainActivity, DogListActivity::class.java))
    }

    private fun openSettingActivity() {
        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}