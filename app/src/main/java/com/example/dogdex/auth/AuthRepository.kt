package com.example.dogdex.auth

import com.example.dogdex.model.User
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.api.DogsApi
import com.example.dogdex.api.dto.LoginDTO
import com.example.dogdex.api.dto.SignUpDTO
import com.example.dogdex.api.dto.UserDTOMapper
import com.example.dogdex.api.makeNetworkCall

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): ApiResponseState<User> =
        makeNetworkCall {
            val loginDTO = LoginDTO(
                email = email,
                password = password
            )
            val loginResponse = DogsApi.retrofitService.login(loginDTO)
            if(!loginResponse.isSuccess){
                throw Exception(loginResponse.message)
            }
            val userDTO = loginResponse.data.user
            val userDtoMapper = UserDTOMapper()
            userDtoMapper.fromUserDTOToUserDomain(userDTO)
        }

    suspend fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ): ApiResponseState<User> =
        makeNetworkCall {
            val signUpDTO = SignUpDTO(
                email = email,
                password = password,
                passwordConfirmation = confirmPassword
            )
            val signUpResponse = DogsApi.retrofitService.signUp(signUpDTO)
            if(!signUpResponse.isSuccess){
                throw Exception(signUpResponse.message)
            }
            val userDTO = signUpResponse.data.user
            val userDtoMapper = UserDTOMapper()
            userDtoMapper.fromUserDTOToUserDomain(userDTO)
        }
}