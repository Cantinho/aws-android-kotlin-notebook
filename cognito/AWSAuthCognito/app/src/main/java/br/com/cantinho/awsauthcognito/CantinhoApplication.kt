package br.com.cantinho.awsauthcognito

import android.app.Application
import br.com.cantinho.awsauthcognito.modules.applicationModule
import org.koin.core.context.startKoin

class CantinhoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin { listOf(applicationModule) }
    }

}