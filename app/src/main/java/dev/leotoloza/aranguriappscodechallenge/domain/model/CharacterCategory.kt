package dev.leotoloza.aranguriappscodechallenge.domain.model

/**
 * Categorías de medios en las que puede aparecer un personaje de Disney.
 * Define la clasificación lógica a nivel de negocio.
 */
enum class CharacterCategory {
    /** Cortometraje */
    SHORT_FILM,

    /** Serie de televisión */
    TV_SHOW,

    /** Videojuego */
    VIDEO_GAME,

    /** Película */
    FILM
}

/**
 * Devuelve la lista de categorías activas para este personaje en la capa de negocio.
 *
 * @return Lista de [CharacterCategory] en las que el personaje tiene al menos una aparición.
 */
fun Character.activeCategories(): List<CharacterCategory> = buildList {
    if (films.isNotEmpty()) add(CharacterCategory.FILM)
    if (shortFilms.isNotEmpty()) add(CharacterCategory.SHORT_FILM)
    if (tvShows.isNotEmpty()) add(CharacterCategory.TV_SHOW)
    if (videoGames.isNotEmpty()) add(CharacterCategory.VIDEO_GAME)
}
