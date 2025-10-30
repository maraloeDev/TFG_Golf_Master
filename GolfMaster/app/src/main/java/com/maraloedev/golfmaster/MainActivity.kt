package com.maraloedev.golfmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.maraloedev.golfmaster.ui.theme.GolfMasterTheme
import com.maraloedev.golfmaster.view.core.navigation.NavigationWrapper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GolfMasterAppContent() }
    }
}

@Composable
fun GolfMasterAppContent() {
    GolfMasterTheme {
        val navController = rememberNavController()
        Surface(color = Color(0xFF0B3D2E)) {
            NavigationWrapper(navController)
        }
    }
}
