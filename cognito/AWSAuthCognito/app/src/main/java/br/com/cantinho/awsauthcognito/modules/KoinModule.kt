package br.com.cantinho.awsauthcognito.modules

import android.app.Application
import br.com.cantinho.awsauthcognito.CantinhoApplication
import br.com.cantinho.awsauthcognito.aws.auth.AwsConfigurationProvider
import org.koin.android.BuildConfig
import org.koin.dsl.module

val applicationModule = module {
    single<Application> { CantinhoApplication() }
    single { (application: Application) -> AwsConfigurationProvider(application.applicationContext, BuildConfig.BUILD_TYPE) }
}