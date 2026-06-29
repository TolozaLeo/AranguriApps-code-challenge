package dev.leotoloza.aranguriappscodechallenge.data.network.mapper

import dev.leotoloza.aranguriappscodechallenge.data.network.dto.CharacterDto
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character

/**
 * Convierte un [CharacterDto] (modelo de red) en un [Character] (modelo de dominio).
 * Esta función de extensión es el único punto de la app responsable de esta transformación,
 * manteniendo el principio de responsabilidad única entre capas.
 */
internal fun CharacterDto.toDomain(): Character = Character(
    id = id,
    name = name,
    imageUrl = imageUrl,
    url = url,
    films = films,
    shortFilms = shortFilms,
    tvShows = tvShows,
    videoGames = videoGames
)
