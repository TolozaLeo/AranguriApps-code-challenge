package dev.leotoloza.aranguriappscodechallenge.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representa los destinos de la navegación inferior/adaptativa en la aplicación.
 *
 * @property route Identificador único de la ruta de navegación.
 * @property title Título visible del destino en la interfaz de usuario en español.
 * @property selectedIcon Icono representativo del destino seleccionado.
 * @property unselectedIcon Icono representativo del destino no seleccionado.
 */
enum class BottomNavigation(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    /**
     * Pantalla que muestra el listado de personajes de Disney.
     */
    CHARACTERS("characters", "Personajes", Icons.Default.Person, Icons.Outlined.Person),

    /**
     * Pantalla que muestra los personajes favoritos del usuario.
     */
    FAVORITES("favorites", "Favoritos", Icons.Default.Favorite, Icons.Default.FavoriteBorder)
}
