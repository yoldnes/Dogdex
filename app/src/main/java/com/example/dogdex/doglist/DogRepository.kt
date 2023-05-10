package com.example.dogdex.doglist

import com.example.dogdex.R
import com.example.dogdex.model.Dog
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.api.DogsApi.retrofitService
import com.example.dogdex.api.dto.AddDogToUserDTO
import com.example.dogdex.api.dto.DogDTOMapper
import com.example.dogdex.api.makeNetworkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DogRepository {

    suspend fun getDogCollecion(): ApiResponseState<List<Dog>> {
        return withContext(Dispatchers.IO) {
            val allDogsListDeferred = async { downloadDogs() }
            val userDogsListDeferred = async { getUserDogs() }

            val allDogsListResponse = allDogsListDeferred.await()
            val userDogsListResponse = userDogsListDeferred.await()

            if (allDogsListResponse is ApiResponseState.Error) {
                allDogsListResponse
            } else if (userDogsListResponse is ApiResponseState.Error) {
                userDogsListResponse
            } else if (allDogsListResponse is ApiResponseState.Success
                && userDogsListResponse is ApiResponseState.Success
            ) {
                ApiResponseState.Success(
                    getCollectionList(
                        allDogsListResponse.data,
                        userDogsListResponse.data
                    )
                )
            } else {
                ApiResponseState.Error(R.string.error_default)
            }
        }
    }

    private fun getCollectionList(
        allDogsList: List<Dog>,
        userDogsList: List<Dog>
    ): List<Dog> {
        return allDogsList.map {
            if (userDogsList.contains(it)) {
                it
            } else {
                Dog(
                    0, it.index, "", "", "", "", "",
                    "", "", "", "", incollection = false
                )
            }
        }.sorted()
    }

    private suspend fun downloadDogs(): ApiResponseState<List<Dog>> =
        makeNetworkCall {
            val dogListApiResponse = retrofitService.getAllDogs()
            val dogDtoList = dogListApiResponse.data.dogs
            val dogDtoMapper = DogDTOMapper()
            dogDtoMapper.fromDogDTOListToDogDomainList(dogDtoList)
        }

    private suspend fun getUserDogs(): ApiResponseState<List<Dog>> =
        makeNetworkCall {
            val dogListApiResponse = retrofitService.getUserDogs()
            val dogDtoList = dogListApiResponse.data.dogs
            val dogDtoMapper = DogDTOMapper()
            dogDtoMapper.fromDogDTOListToDogDomainList(dogDtoList)
        }

    suspend fun addDogToUser(dogId: Long): ApiResponseState<Any> =
        makeNetworkCall {
            val addDogToUserDTO = AddDogToUserDTO(dogId)
            val defaultResponse = retrofitService.addDogToUser(addDogToUserDTO)

            if (!defaultResponse.isSuccess) {
                throw Exception(defaultResponse.message)
            }
        }

}