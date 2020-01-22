package br.com.cantinho.awsauthcognito.aws.auth

import br.com.cantinho.awsauthcognito.aws.auth.events.*
import br.com.cantinho.awsauthcognito.utils.Failure
import br.com.cantinho.awsauthcognito.utils.Result
import br.com.cantinho.awsauthcognito.utils.Success
import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.util.StringUtils
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.StringBuilder

class CognitoService(val awsConfigurationProvider: AwsConfigurationProvider, val userPool: CognitoUserPool, val credentialsProvider: CognitoCredentialsProvider, val identityIdCache: IdentityIdCache?) {

    val identityUserPoolAppClientId: String
    val cognitoProviderUrl: String
    val inMemoryIdentityId: MutableMap<String, String> = mutableMapOf()
    init {
        try {
            val optJsonObject = awsConfigurationProvider.awsConfiguration.optJsonObject("CognitoUserPool")
            identityUserPoolAppClientId = optJsonObject.getString("AppClientId")
            val region = optJsonObject.get("Region")
            val userPoolId = optJsonObject.get("PoolId")
            cognitoProviderUrl = StringBuilder()
                .append("cognito-idp.")
                .append(region)
                .append(".amazonaws.com/")
                .append(userPoolId).toString()
        } catch (exc: Exception) {
            throw IllegalArgumentException("Unable to initialize ${CognitoService::class.qualifiedName}.Verify your aws configuration file.", exc)
        }
    }

    fun getCachedIdentityId(username: String): String {
        if(identityIdCache == null) {
            return inMemoryIdentityId[username] ?: ""
        }
        val cachedIdentityId = identityIdCache.read(username)
        if(StringUtils.isBlank(cachedIdentityId)) {
            val refreshedIdentityId = credentialsProvider.identityPoolId
            identityIdCache.store(username, refreshedIdentityId)
            return refreshedIdentityId
        }
        return cachedIdentityId
    }

    fun currentUserId() = userPool.currentUser.userId

    inline fun getUserSession(userId: String, password: String, crossinline onGetUserSession: (e: Result<Event>) -> Unit) {
        val authenticationHandler = object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession, newDevice: CognitoDevice?) {
                setIdTokenToCredentialsProvider(userSession)
                onGetUserSession.invoke(Success(GetUserSession()))
            }

            override fun onFailure(exception: Exception) {
                onGetUserSession.invoke(Failure(exception))
            }

            override fun getAuthenticationDetails(
                authenticationContinuation: AuthenticationContinuation,
                userId: String?
            ) {
                onGetUserSession.invoke(Success(GetAuthenticationDetails()))
                authenticationContinuation.setAuthenticationDetails(AuthenticationDetails(userId, password, null))
                authenticationContinuation.continueTask()
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {
                onGetUserSession.invoke(Success(AuthenticationChallenge()))
            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation) {
                onGetUserSession.invoke(Success(GetMFACode()))
                continuation.continueTask()
            }
        }
        userPool.getUser(userId).getSessionInBackground(authenticationHandler)
    }

    protected fun setIdTokenToCredentialsProvider(userSession: CognitoUserSession) {
        val idToken = userSession.idToken.jwtToken
        val logins: MutableMap<String, String> = hashMapOf()
        logins.put(cognitoProviderUrl, idToken)
        credentialsProvider.logins
    }


}