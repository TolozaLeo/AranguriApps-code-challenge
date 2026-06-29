package dev.leotoloza.aranguriappscodechallenge.domain.model

/**
 * Criterio de búsqueda/filtrado de personajes contra la API de Disney.
 * Cada subclase representa un parámetro de query soportado por el endpoint `GET /character`.
 *
 * La API solo permite aplicar **un criterio por request**. Este diseño como `sealed class`
 * hace imposible en tiempo de compilación crear un filtro vacío o con múltiples criterios
 * simultáneos, garantizando el uso correcto del contrato.
 *
 * Parámetros de query verificados contra la API real:
 * - `name` → [ByName]
 * - `films` → [ByFilm]
 * - `tvShows` → [ByTvShow]
 * - `shortFilms` → [ByShortFilm]
 * - `videoGames` → [ByVideoGame]
 */
sealed class CharacterFilter {

    /** Filtra personajes cuyo nombre contenga [value]. */
    data class ByName(val value: String) : CharacterFilter()

    /** Filtra personajes que hayan aparecido en la película con nombre [value]. */
    data class ByFilm(val value: String) : CharacterFilter()

    /** Filtra personajes que hayan aparecido en el programa de TV con nombre [value]. */
    data class ByTvShow(val value: String) : CharacterFilter()

    /** Filtra personajes que hayan aparecido en el cortometraje con nombre [value]. */
    data class ByShortFilm(val value: String) : CharacterFilter()

    /** Filtra personajes que hayan aparecido en el videojuego con nombre [value]. */
    data class ByVideoGame(val value: String) : CharacterFilter()
}
