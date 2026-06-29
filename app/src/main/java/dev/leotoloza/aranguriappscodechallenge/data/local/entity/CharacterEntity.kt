package dev.leotoloza.aranguriappscodechallenge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character

/**
 * Entidad de Room que representa un personaje favorito en la base de datos local.
 *
 * @property id Identificador único del personaje (Clave Primaria).
 * @property name Nombre del personaje.
 * @property imageUrl URL de la imagen del personaje.
 * @property url URL de la API del personaje.
 * @property films Lista de películas asociadas al personaje.
 * @property shortFilms Lista de cortometrajes asociados al personaje.
 * @property tvShows Lista de series de televisión asociadas al personaje.
 * @property videoGames Lista de videojuegos asociados al personaje.
 */
@Entity(tableName = "favorite_characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val url: String,
    val films: List<String>,
    val shortFilms: List<String>,
    val tvShows: List<String>,
    val videoGames: List<String>
)

/**
 * Convierte una [CharacterEntity] al modelo de dominio [Character].
 *
 * @return Instancia del modelo de dominio [Character].
 */
fun CharacterEntity.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        imageUrl = imageUrl,
        url = url,
        films = films,
        shortFilms = shortFilms,
        tvShows = tvShows,
        videoGames = videoGames
    )
}

/**
 * Convierte un [Character] del modelo de dominio a su entidad local [CharacterEntity].
 *
 * @return Instancia de la entidad [CharacterEntity].
 */
fun Character.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        url = url,
        films = films,
        shortFilms = shortFilms,
        tvShows = tvShows,
        videoGames = videoGames
    )
}
