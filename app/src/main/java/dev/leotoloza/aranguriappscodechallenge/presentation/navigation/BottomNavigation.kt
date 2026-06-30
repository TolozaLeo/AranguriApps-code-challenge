package dev.leotoloza.aranguriappscodechallenge.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import dev.leotoloza.aranguriappscodechallenge.R

/**
 * Representa los destinos de la navegación inferior/adaptativa en la aplicación.
 *
 * @property route Identificador único de la ruta de navegación.
 * @property titleResId Recurso de texto para el título visible en español.
 * @property selectedIcon Icono representativo del destino seleccionado.
 * @property unselectedIcon Icono representativo del destino no seleccionado.
 */
enum class BottomNavigation(
    val route: String,
    val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    /**
     * Pantalla que muestra el listado de personajes de Disney.
     */
    CHARACTERS("characters", R.string.characters_title, Icons.Default.Person, Icons.Outlined.Person),

    /**
     * Pantalla que muestra los personajes favoritos del usuario.
     */
    FAVORITES("favorites", R.string.favorites_title, Icons.Default.Favorite, Icons.Default.FavoriteBorder)
}
