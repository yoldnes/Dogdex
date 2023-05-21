package com.example.dogdex.Main

import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.doglist.DogRepository
import com.example.dogdex.machinelearning.ClassifierRepository
import com.example.dogdex.machinelearning.DogRecognition
import com.example.dogdex.model.Dog
import com.hackaprende.dogedex.machinelearning.Classifier
import kotlinx.coroutines.launch
import java.nio.MappedByteBuffer

class MainViewModel : ViewModel() {

    private val _dog = MutableLiveData<Dog>()
    val dog: LiveData<Dog> get() = _dog

    private val _status = MutableLiveData<ApiResponseState<Dog>>()
    val status: LiveData<ApiResponseState<Dog>> get() = _status

    private val _dogRecognition = MutableLiveData<DogRecognition>()
    val dogRecognition: LiveData<DogRecognition> get() = _dogRecognition

    private val dogRepository = DogRepository()
    private lateinit var classifierRepository: ClassifierRepository

    fun setUpClassifier(tfLiteModel: MappedByteBuffer, labels: List<String>) {
        val classifier = Classifier(tfLiteModel, labels)
        classifierRepository = ClassifierRepository(classifier)
    }

    fun recognizeImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            _dogRecognition.value = classifierRepository.recognizeImage(imageProxy)
            imageProxy.close()
        }
    }

    fun getDogByMLId(dogId: String) {
        viewModelScope.launch {
            handleResponseStatus(dogRepository.getDogByMLId(dogId))
        }
    }

    private fun handleResponseStatus(apiResponseStatus: ApiResponseState<Dog>) {
        if (apiResponseStatus is ApiResponseState.Success) {
            _dog.value = apiResponseStatus.data!!
        }
        _status.value = apiResponseStatus
    }
}