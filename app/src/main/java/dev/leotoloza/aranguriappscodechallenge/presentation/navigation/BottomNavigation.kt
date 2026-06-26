package dev.leotoloza.aranguriappscodechallenge.presentation.navigation

/**
 * Representa los destinos de la navegación inferior/adaptativa en la aplicación.
 *
 * @property route Identificador único de la ruta de navegación.
 * @property title Título visible del destino en la interfaz de usuario en español.
 * @property iconText Representación textual temporal del ícono del destino.
 */
enum class BottomNavigation(
    val route: String,
    val title: String,
    val iconText: String
) {
    /**
     * Pantalla que muestra el listado de personajes de Disney.
     */
    CHARACTERS("characters", "Personajes", "P"),

    /**
     * Pantalla que muestra los personajes favoritos del usuario.
     */
    FAVORITES("favorites", "Favoritos", "F")
}
