package com.example.myapplication.model

import java.lang.Error

data class UserFetchCommand(
    val userName: String,
    val passWord: String,
) {

    fun <T> execute(
        userProvider: (String, String) -> Result<UserData, Error>,
        onSuccess: (UserData) ->
        T,
        onError: (Error) -> T,
    ): T {
        return when (val result = userProvider(userName, passWord)) {
            is Success -> onSuccess(result.value)
            is Failure -> onError(result.reason)
        }
    }

}