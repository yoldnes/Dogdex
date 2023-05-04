package com.example.dogdex.auth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dogdex.R
import com.example.dogdex.databinding.FragmentLoginBinding
import com.example.dogdex.isValidEmail


class LoginFragment : Fragment() {

    interface LoginFragmentAction {
        fun onRegisterButtomClick()
        fun onLoginButtomClick(email: String, password: String)
    }

    private lateinit var loginFragmentAction: LoginFragmentAction
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginFragmentAction = try {
            context as LoginFragmentAction
        } catch (e: ClassCastException) {
            throw ClassCastException("$context ")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {
            loginRegisterButton.setOnClickListener {
                loginFragmentAction.onRegisterButtomClick()
            }
            loginButton.setOnClickListener {
                validateFields()
            }
        }
    }

    private fun validateFields() {
        with(binding) {
            emailInput.error = ""
            passwordInput.error = ""

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
            loginFragmentAction.onLoginButtomClick(email,password)
        }
    }

}