package com.odc.prixdumarche

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.odc.prixdumarche.ui.navigation.PrixDuMarcheNavGraph
import com.odc.prixdumarche.ui.theme.PrixDuMarcheTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            PrixDuMarcheTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PrixDuMarcheNavGraph()
                }
            }
        }
    }
}
