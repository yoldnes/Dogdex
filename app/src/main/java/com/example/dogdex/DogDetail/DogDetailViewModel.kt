package com.example.dogdex.DogDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.doglist.DogRepository
import kotlinx.coroutines.launch

class DogDetailViewModel : ViewModel() {

    private val _status = MutableLiveData<ApiResponseState<Any>>()
    val status: LiveData<ApiResponseState<Any>> get() = _status

    private val repository = DogRepository()

    fun addDogToUser(dogId: Long) {
        viewModelScope.launch {
            _status.value = ApiResponseState.Loading()
            handleAddDogToUserResponseStatus(repository.addDogToUser(dogId))
        }
    }

    private fun handleAddDogToUserResponseStatus(apiResponseStatus: ApiResponseState<Any>) {
        _status.value = apiResponseStatus
    }
}