package com.example.dogdex.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.dogdex.Main.MainActivity
import com.example.dogdex.R
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.databinding.ActivityLoginBinding
import com.example.dogdex.model.User

class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentAction,
    SignUpFragment.SignUpFragmentAction {

    private val userViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onObserver(binding)
    }

    private fun onObserver(binding: ActivityLoginBinding) {
        userViewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseState.Loading -> binding.progress.visibility = View.VISIBLE
                is ApiResponseState.Success -> binding.progress.visibility = View.GONE
                is ApiResponseState.Error -> {
                    binding.progress.visibility = View.GONE
                    showErrorDialog(status.messageId)
                }
            }
        }

        userViewModel.user.observe(this) { user ->
            if (user != null) {
                User.setLoggedInUser(this, user)
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showErrorDialog(messageId: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.there_was_on_error)
            .setMessage(messageId)
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()
            .show()
    }

    override fun onSignUprButtomClick(
        email: String,
        password: String,
        confirmPassword: String
    ) {
        userViewModel.signUp(email, password, confirmPassword)
    }

    override fun onLoginButtomClick(email: String, password: String) {
        userViewModel.login(email, password)
    }

    override fun onRegisterButtomClick() {
        findNavController(R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }
}