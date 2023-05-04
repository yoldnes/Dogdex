package com.example.dogdex.api

import com.example.dogdex.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException

private const val UNAUTHORIZED_ERROR_CODE = 401
suspend fun <T> makeNetworkCall(
    call: suspend () -> T
): ApiResponseState<T> {
    return withContext(Dispatchers.IO) {
        try {
            ApiResponseState.Success(call())
        } catch (e: UnknownHostException) {
            ApiResponseState.Error(R.string.error_conexion_message)
        } catch (e: HttpException) {
           val messageError = if (e.code() == UNAUTHORIZED_ERROR_CODE) {
               R.string.wrong_user_or_password
            } else {
                R.string.error_default
            }
            ApiResponseState.Error(messageError)
        } catch (e: Exception) {
            val message = when (e.message) {
                "sign_up_error" -> R.string.error_sign_up
                "sign_in_error" -> R.string.error_sign_in
                "user_already_exists" -> R.string.error_user_already_exissts
                else -> R.string.error_default
            }
            ApiResponseState.Error(R.string.error_default)
        }
    }
}