package com.example.dogdex.doglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogdex.model.Dog
import com.example.dogdex.api.ApiResponseState
import kotlinx.coroutines.launch

class DogListViewModel : ViewModel() {

    private val _dogList = MutableLiveData<List<Dog>>()
    val dogList: LiveData<List<Dog>> get() = _dogList

    private val _status = MutableLiveData<ApiResponseState<Any>>()
    val status: LiveData<ApiResponseState<Any>> get() = _status

    private val repository = DogRepository()

    init {
        getDogCollection()
    }

    private fun getDogCollection() {
        viewModelScope.launch {
            _status.value = ApiResponseState.Loading()
            handleResponseStatus(repository.getDogCollecion())
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleResponseStatus(apiResponseStatus: ApiResponseState<List<Dog>>) {
        if (apiResponseStatus is ApiResponseState.Success) {
            _dogList.value = apiResponseStatus.data!!
        }
        _status.value = apiResponseStatus as ApiResponseState<Any>
    }
}