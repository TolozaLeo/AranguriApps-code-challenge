package dev.leotoloza.aranguriappscodechallenge.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.leotoloza.aranguriappscodechallenge.R
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.DisneyCelestialBlue
import kotlinx.coroutines.delay

/**
 * Duración en milisegundos que se muestra la pantalla de bienvenida.
 */
private const val SPLASH_TIMEOUT_MS = 2000L

/**
 * Pantalla de bienvenida (Splash Screen) de la aplicación.
 *
 * Muestra el logotipo de Disney (silueta de Mickey), el título de la aplicación
 * y el crédito del desarrollador en un fondo de color azul celestial (#238EC1).
 * Transiciona automáticamente mediante el callback [onTimeout] después de 1000ms.
 *
 * @param onTimeout Callback invocado al transcurrir el tiempo definido de visualización.
 * @param modifier Modificador para aplicar al contenedor.
 */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_TIMEOUT_MS)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DisneyCelestialBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_disney_castle),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.splash_app_title),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.splash_created_by),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
