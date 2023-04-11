package com.example.dogdex.api

import com.example.dogdex.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> makeNetworkCall(
    call: suspend () -> T
): ApiResponseState<T> {
    return withContext(Dispatchers.IO) {
        try {
            ApiResponseState.Success(call())
        } catch (e: Exception) {
            ApiResponseState.Error(R.string.error_conexion_message)
        }catch (e:Exception){
            ApiResponseState.Error(R.string.error_default)
        }
    }
}