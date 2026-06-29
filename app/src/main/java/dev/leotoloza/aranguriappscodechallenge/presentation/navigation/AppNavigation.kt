package dev.leotoloza.aranguriappscodechallenge.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.leotoloza.aranguriappscodechallenge.presentation.characters.CharactersScreen
import dev.leotoloza.aranguriappscodechallenge.presentation.details.DetailsScreen
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
    var selectedCharacterName by remember { mutableStateOf<String?>(null) }

    val customItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ), navigationRailItemColors = NavigationRailItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    val currentCharacterName = selectedCharacterName
    if (currentCharacterName != null) {
        // Intercepta el gesto de retroceso del sistema en la pantalla de detalle
        BackHandler(enabled = true) {
            // Regresa a la pantalla origen (currentDestination) al quitar el personaje seleccionado
            selectedCharacterName = null
        }
        DetailsScreen(
            characterName = currentCharacterName,
            onBack = { selectedCharacterName = null },
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
    } else {
        // Configura el manejador de retroceso según la pantalla activa
        if (currentDestination == BottomNavigation.FAVORITES) {
            BackHandler(enabled = true) {
                // Si está en Favoritos, al presionar atrás regresa a la pantalla de Personajes
                currentDestination = BottomNavigation.CHARACTERS
            }
        } else {
            BackHandler(enabled = true) {
                // Si está en Personajes (la raíz), el retroceso no tiene acción para evitar que se cierre la app
            }
        }

        NavigationSuiteScaffold(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                navigationBarContainerColor = MaterialTheme.colorScheme.background,
                navigationRailContainerColor = MaterialTheme.colorScheme.background
            ),
            navigationSuiteItems = {
                BottomNavigation.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    item(
                        selected = isSelected, onClick = {
                        selectedCharacterName = null
                        currentDestination = destination
                    }, icon = {
                        Column(
                            modifier = Modifier
                                .width(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = destination.iconText,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = destination.title,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }, colors = customItemColors
                    )
                }
            }) {
            // Renderizado del contenido según el destino actual
            when (currentDestination) {
                BottomNavigation.CHARACTERS -> {
                    CharactersScreen(
                        onCharacterClick = { name -> selectedCharacterName = name },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }

                BottomNavigation.FAVORITES -> {
                    FavoritesScreen(
                        onCharacterClick = { name -> selectedCharacterName = name },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }
}
