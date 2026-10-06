package com.odc.prixdumarche

import android.app.Application
import com.odc.prixdumarche.data.AppContainer
import com.odc.prixdumarche.data.DefaultAppContainer

class PrixDuMarcheApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
