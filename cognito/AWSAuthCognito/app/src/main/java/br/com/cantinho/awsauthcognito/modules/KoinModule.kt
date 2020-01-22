package br.com.cantinho.awsauthcognito.modules

import android.app.Application
import br.com.cantinho.awsauthcognito.CantinhoApplication
import br.com.cantinho.awsauthcognito.aws.auth.AwsConfigurationProvider
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import org.koin.android.BuildConfig
import org.koin.dsl.module

val applicationModule = module {
    single<Application> { CantinhoApplication() }
    single { (application: Application) -> AwsConfigurationProvider(application.applicationContext, BuildConfig.BUILD_TYPE) }
    single { (application: Application, awsConfigurationProvider: AwsConfigurationProvider) -> CognitoUserPool(application.applicationContext, awsConfigurationProvider.awsConfiguration) }
    single<CognitoCredentialsProvider> { (application: Application, awsConfigurationProvider: AwsConfigurationProvider) -> CognitoCachingCredentialsProvider(application.applicationContext, awsConfigurationProvider.awsConfiguration) }

}