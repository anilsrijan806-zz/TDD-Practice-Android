package com.example.myapplication.authentication

import com.example.myapplication.model.UserFetchCommand

sealed class AuthenticationResult{
    data class Error(val list: List<ValidationError?>): AuthenticationResult()
   data class Partial(val userFetchCommand: UserFetchCommand) : AuthenticationResult()
}

sealed class ValidationError {
    object EmptyUsername : ValidationError()
    object EmptyPassword : ValidationError()
}