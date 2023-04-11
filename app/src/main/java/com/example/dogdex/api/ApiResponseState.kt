package com.example.dogdex.api

sealed class ApiResponseState<T> {
    class Success<T>(val data: T) : ApiResponseState<T>()
    class Loading<T> : ApiResponseState<T>()
    class Error<T>(val messageId: Int) : ApiResponseState<T>()
}