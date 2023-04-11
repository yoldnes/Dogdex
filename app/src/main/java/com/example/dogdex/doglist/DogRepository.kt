package com.example.dogdex.doglist

import com.example.dogdex.Dog
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.api.DogsApi
import com.example.dogdex.api.dto.DogDTOMapper
import com.example.dogdex.api.makeNetworkCall

class DogRepository {

    suspend fun downloadDogs(): ApiResponseState<List<Dog>> =
         makeNetworkCall {
            val dogListApiResponse = DogsApi.retrofitService.getAllDogs()
            val dogDtoList = dogListApiResponse.data.dogs
            val dogDtoMapper = DogDTOMapper()
            dogDtoMapper.fromDogDTOListToDogDomainList(dogDtoList)
        }
}