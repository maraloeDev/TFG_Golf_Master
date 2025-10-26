package com.maraloedev.golfmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.ui.theme.GolfMasterTheme
import com.maraloedev.golfmaster.view.core.navigation.NavigationWrapper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GolfMasterTheme {
                val navController = rememberNavController()
                NavigationWrapper(navController)
            }
        }
    }
}
