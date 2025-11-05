package com.maraloedev.golfmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.view.core.navigation.MainNavHost
import com.maraloedev.golfmaster.ui.theme.GolfMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GolfMasterTheme {
                val navController = rememberNavController()
                MainNavHost(navController)
            }
        }
    }
}
