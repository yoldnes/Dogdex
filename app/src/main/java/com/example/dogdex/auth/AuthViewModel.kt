package com.example.dogdex.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.model.User
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _status = MutableLiveData<ApiResponseState<User>>()
    val status: LiveData<ApiResponseState<User>> get() = _status

    private val repository = AuthRepository()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _status.value = ApiResponseState.Loading()
            handleResponseStatus(repository.login(email, password))
        }
    }

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _status.value = ApiResponseState.Loading()
            handleResponseStatus(repository.signUp(email, password, confirmPassword))
        }
    }

    private fun handleResponseStatus(apiResponseStatus: ApiResponseState<User>) {
        if (apiResponseStatus is ApiResponseState.Success) {
            _user.value = apiResponseStatus.data!!
        }
        _status.value = apiResponseStatus
    }
}