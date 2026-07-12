package com.matchora.app

import com.matchora.app.data.model.*
import com.matchora.app.ui.theme.MatchoraTheme
import com.matchora.app.util.Session
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.matchora.app.navigation.MatchoraNavHost
import com.matchora.app.ui.screens.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MatchoraTheme {
                val navController = rememberNavController()
                MatchoraNavHost(
                    navController = navController,
                    session = session,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
