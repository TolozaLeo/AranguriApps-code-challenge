package dev.leotoloza.aranguriappscodechallenge.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO que representa el objeto `data` de un personaje en la respuesta de la API de Disney.
 * Los campos [allies], [enemies] y [parkAttractions] son ignorados intencionalmente,
 * conforme a la especificación de diseño.
 *
 * @property id Identificador único del personaje (mapeado desde `_id`).
 * @property name Nombre del personaje.
 * @property imageUrl URL de la imagen del personaje.
 * @property url URL del recurso en la API.
 * @property films Lista de películas en las que aparece.
 * @property shortFilms Lista de cortometrajes en los que aparece.
 * @property tvShows Lista de series de televisión en las que aparece.
 * @property videoGames Lista de videojuegos en los que aparece.
 */
@JsonClass(generateAdapter = true)
data class CharacterDto(
    @Json(name = "_id") val id: Int,
    val name: String,
    val imageUrl: String,
    val url: String,
    val films: List<String>,
    val shortFilms: List<String>,
    val tvShows: List<String>,
    val videoGames: List<String>
)
