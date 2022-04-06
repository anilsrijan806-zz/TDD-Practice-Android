package com.example.myapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapplication.authentication.AuthenticationResult
import com.example.myapplication.authentication.ValidationError
import com.example.myapplication.model.*
import org.junit.Rule
import org.junit.Test
import java.util.regex.Pattern
import kotlin.math.exp


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestCasesPractice {

    val valid_username = "abcd"
    val valid_password = "password@1"

    data class MockError(
        override val message: String
    ) : Error()


    fun userDataProviderForTest(): UserData {
        return UserData(userName = "")
    }

    fun anotherUserDataProviderForTest(): UserData {
        return UserData(userName = "new")
    }

    @Test
    fun testItShouldAutheticateBasedOnUsernameAndPassword() {
        val authenticator = UserAuthenticator()
        authenticator.authenticate(valid_username, valid_password)
    }

    @Test
    fun testItShouldReturnAnErrorWhenUsernameIsEmpty() {
        val authenticator = UserAuthenticator()
        val result: AuthenticationResult = authenticator.authenticate("", valid_password)

        val list: AuthenticationResult.Error =
            AuthenticationResult.Error(mutableListOf(ValidationError.EmptyUsername))

        assert(result == list)
    }

    @Test
    fun testItShouldReturnAnErrorWhenPasswordIsEmpty() {
        val authenticator = UserAuthenticator()
        val result = authenticator.authenticate(valid_username, "")

        val list = AuthenticationResult.Error(mutableListOf(ValidationError.EmptyPassword))

        assert(result == list)
    }

    @Test
    fun testItShouldReturnAnErrorWhenUsernameAndPasswordIsEmpty() {
        val authenticator = UserAuthenticator()
        val result = authenticator.authenticate(username = "", password = "")

        val list = AuthenticationResult.Error(
            mutableListOf(
                ValidationError.EmptyUsername,
                ValidationError.EmptyPassword
            )
        )

        assert(result == list)
    }


    @Test
    fun testItShouldReturnPartialCommandIfValidationsAreSuccessful() {
        val authenticator = UserAuthenticator()
        val result =
            authenticator.authenticate(username = valid_username, password = valid_password)

        val list = AuthenticationResult.Partial(
            UserFetchCommand(valid_username, valid_password)
        )

        assert(result == list)
    }

    @Test
    fun testItShouldReturnCommandForGivenUserNameAndPassword() {
        val authenticator = UserAuthenticator()
        val result = authenticator.authenticate(username = "abcd", password = "password")

        val list = AuthenticationResult.Partial(
            UserFetchCommand("abcd", "password")
        )

        assert(result == list)
    }


    @Test
    fun testUSerFetchCommandExecutesItselfAndProvidesErrorIfUSerNotFound() {
        val command = UserFetchCommand(userName = "abcd", passWord = "password")

        val expectedError = MockError(message = "error")

        val userProvider: (String, String) -> Result<UserData, Error> = { _: String, _: String ->
            Failure(expectedError)
        }

        val didWeReceivedError = command.execute(
            userProvider,
            onSuccess = {
                false
            },
            onError = {
                it is MockError
            }
        )

        assert(didWeReceivedError)

    }


    @Test
    fun testUserFetchCommandExecutesItselfProvidesTheErrorFromResult() {
        val command = UserFetchCommand(userName = "abcd", passWord = "password")

        val expectedError = MockError(message = "anotherError")


        val userProvider: (String, String) -> Result<UserData, Error> = { _: String, _: String ->
            Failure(expectedError)
        }

        val didWeReceivedError = command.execute(
            userProvider,
            onSuccess = {
                false
            },
            onError = {
                it is MockError
            }
        )

        assert(didWeReceivedError)

    }


    @Test
    fun testUserFetchCommandExecutesItselfAndProvidesTheUserDataOnSuccess() {
        val command = UserFetchCommand(userName = "abcd", passWord = "password")

        val expectedUser = userDataProviderForTest()

        val userProvider: (String, String) -> Result<UserData, Error> = { _: String, _: String ->
            Success(expectedUser)
        }

        val didWeReceivedUser = command.execute(
            userProvider,
            onSuccess = { userData ->
                userData == expectedUser
            },
            onError = {
                false
            }
        )


        assert(didWeReceivedUser)

    }


    @Test
    fun testUserFetchCommandExecutesWithGivenUserNameAndPassword() {
        val command = UserFetchCommand(userName = "abcd", passWord = "password")

        val expectedUser = anotherUserDataProviderForTest()

        val userProvider: (String, String) -> Result<UserData, Error> = { userName, passWord ->
            if (userName == "abcd" && passWord == "password") {
                Success(anotherUserDataProviderForTest())
            }
            Failure(MockError(message = "Should Not Happen"))
        }

        val didWeReceivedUser = command.execute(
            userProvider,
            onSuccess = { userData ->
                userData == expectedUser
            },
            onError = {
                false
            }
        )
        assert(didWeReceivedUser)

    }

}