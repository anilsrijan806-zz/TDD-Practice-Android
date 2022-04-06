package com.example.myapplication

import com.example.myapplication.authentication.AuthenticationResult
import com.example.myapplication.authentication.ValidationError
import com.example.myapplication.model.UserFetchCommand

class UserAuthenticator {

    fun authenticate(username: String, password: String): AuthenticationResult {

        val credentialValidations = mutableListOf<ValidationError?>()

        credentialValidations.add(validateUsername(username))
        credentialValidations.add(validatePassword(password))

        val filter = credentialValidations.filterNotNull()

        return if (filter.isNotEmpty()) {
            AuthenticationResult.Error(filter.toMutableList())
        } else {
            AuthenticationResult.Partial(userFetchCommand = UserFetchCommand(username, password))
        }

    }

    private fun validateUsername(username: String): ValidationError? {
        if (username.isEmpty()) {
            return ValidationError.EmptyUsername
        }
        return null
    }


    private fun validatePassword(username: String): ValidationError? {
        if (username.isEmpty()) {
            return ValidationError.EmptyPassword
        }
        return null
    }


}