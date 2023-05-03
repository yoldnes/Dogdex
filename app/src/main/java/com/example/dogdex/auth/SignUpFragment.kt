package com.example.dogdex.auth

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dogdex.R
import com.example.dogdex.databinding.FragmentSignUpBinding
import com.example.dogdex.isValidEmail

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var singUpFragmentAction: SignUpFragmentAction

    interface SignUpFragmentAction {
        fun onSignUprButtomClick(email: String, password: String, confirmPassword: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        singUpFragmentAction = try {
            context as SignUpFragmentAction
        } catch (e: ClassCastException) {
            throw ClassCastException("$context ")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        setupSignUpButton()
        return binding.root
    }

    private fun setupSignUpButton() {
        with(binding) {
            signUpButton.setOnClickListener {
                validateFields()
            }
        }
    }

    private fun validateFields() {
        with(binding) {
            emailInput.error = ""
            passwordInput.error = ""
            confirmPasswordInput.error = ""

            val email = emailEdit.text.toString()
            if (!isValidEmail(email)){
                emailInput.error = getString(R.string.email_is_not_valid)
                return
            }
            val password = passwordEdit.text.toString()
            if (password.isEmpty()){
                passwordInput.error = getString(R.string.password_must_not_be_empty)
                return
            }

            val confirmPassword = confirmPasswordEdit.text.toString()
            if (confirmPassword.isEmpty()){
                confirmPasswordInput.error = getString(R.string.password_must_not_be_empty)
                return
            }
            if (password != confirmPassword){
                confirmPasswordInput.error = getString(R.string.password_do_not_match)
                return
            }

            singUpFragmentAction.onSignUprButtomClick(email,password,confirmPassword)
        }
    }
}