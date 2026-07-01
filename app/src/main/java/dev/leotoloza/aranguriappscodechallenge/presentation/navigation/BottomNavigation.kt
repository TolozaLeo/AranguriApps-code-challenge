package dev.leotoloza.aranguriappscodechallenge.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.vector.ImageVector
import dev.leotoloza.aranguriappscodechallenge.R

/**
 * Representa los diferentes tipos de recursos para un icono.
 */
sealed class IconResource {
    data class Vector(val imageVector: ImageVector) : IconResource()
    data class Drawable(@param:DrawableRes val id: Int) : IconResource()
}

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
    val selectedIcon: IconResource,
    val unselectedIcon: IconResource
) {
    /**
     * Pantalla que muestra el listado de personajes de Disney.
     */
    CHARACTERS(
        route = "characters",
        titleResId = R.string.characters_title,
        selectedIcon = IconResource.Drawable(R.drawable.ic_characters_selected),
        unselectedIcon = IconResource.Drawable(R.drawable.ic_characters_unselected)
    ),

    /**
     * Pantalla que muestra los personajes favoritos del usuario.
     */
    FAVORITES(
        route = "favorites",
        titleResId = R.string.favorites_title,
        selectedIcon = IconResource.Vector(Icons.Default.Favorite),
        unselectedIcon = IconResource.Vector(Icons.Default.FavoriteBorder)
    )
}
