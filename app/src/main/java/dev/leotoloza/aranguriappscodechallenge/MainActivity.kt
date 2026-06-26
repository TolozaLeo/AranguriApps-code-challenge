package dev.leotoloza.aranguriappscodechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.leotoloza.aranguriappscodechallenge.presentation.navigation.AppNavigation
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AranguriAppsCodeChallengeTheme

/**
 * Actividad principal de la aplicación que sirve como punto de entrada.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AranguriAppsCodeChallengeTheme {
                AppNavigation()
            }
        }
    }
}