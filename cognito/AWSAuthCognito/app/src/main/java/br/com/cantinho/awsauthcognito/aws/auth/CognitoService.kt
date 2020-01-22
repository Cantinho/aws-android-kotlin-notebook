package br.com.cantinho.awsauthcognito.aws.auth

import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.StringBuilder

class CognitoService(val awsConfigurationProvider: AwsConfigurationProvider, val userPool: CognitoUserPool, val credentialsProvider: CognitoCredentialsProvider) {

    val identityUserPoolAppClientId: String
    val cognitoProviderUrl: String
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


}