package br.com.cantinho.awsauthcognito.aws.auth

import android.content.Context
import com.amazonaws.mobile.config.AWSConfiguration

class AwsConfigurationProvider(val context: Context, val configurationName: String = "") {
    val awsConfiguration: AWSConfiguration

    init {
        awsConfiguration = AWSConfiguration(context)
        awsConfiguration.configuration = configurationName
    }
}