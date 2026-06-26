package dev.leotoloza.aranguriappscodechallenge.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.leotoloza.aranguriappscodechallenge.presentation.characters.CharactersScreen
import dev.leotoloza.aranguriappscodechallenge.presentation.favorites.FavoritesScreen

/**
 * Componible principal que gestiona la navegación de la aplicación DisneyApp.
 *
 * Utiliza [NavigationSuiteScaffold] para adaptarse a diferentes tamaños de pantalla (móvil, tableta).
 *
 * @param modifier Modificador para aplicar a la estructura de navegación.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    // Estado del destino seleccionado actualmente
    var currentDestination by remember { mutableStateOf(BottomNavigation.CHARACTERS) }

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            BottomNavigation.entries.forEach { destination ->
                item(
                    selected = currentDestination == destination,
                    onClick = { currentDestination = destination },
                    icon = { Text(destination.iconText) },
                    label = { Text(destination.title) }
                )
            }
        }
    ) {
        // Renderizado del contenido según el destino actual
        when (currentDestination) {
            BottomNavigation.CHARACTERS -> {
                CharactersScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
            BottomNavigation.FAVORITES -> {
                FavoritesScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }
    }
}
