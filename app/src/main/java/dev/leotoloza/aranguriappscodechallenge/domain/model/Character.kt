package dev.leotoloza.aranguriappscodechallenge.domain.model

/**
 * Modelo de dominio que representa un personaje de Disney.
 * Campos mapeados desde la API, descartando [allies], [enemies] y [parkAttractions]
 * según la especificación de diseño.
 *
 * @property id Identificador único del personaje (mapeado desde `_id`).
 * @property name Nombre del personaje.
 * @property imageUrl URL de la imagen del personaje.
 * @property url URL del recurso en la API de Disney.
 * @property films Lista de películas en las que aparece el personaje.
 * @property shortFilms Lista de cortometrajes en los que aparece el personaje.
 * @property tvShows Lista de series de televisión en las que aparece el personaje.
 * @property videoGames Lista de videojuegos en los que aparece el personaje.
 */
data class Character(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val url: String,
    val films: List<String>,
    val shortFilms: List<String>,
    val tvShows: List<String>,
    val videoGames: List<String>
)
