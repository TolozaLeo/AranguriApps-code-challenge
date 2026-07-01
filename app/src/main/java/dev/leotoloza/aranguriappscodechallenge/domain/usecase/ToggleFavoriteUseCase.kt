package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import javax.inject.Inject

/**
 * Caso de uso para alternar el estado de favorito de un personaje de Disney.
 *
 * @property repository El repositorio de personajes.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    /**
     * Alterna la pertenencia del personaje seleccionado a la lista de favoritos.
     *
     * @param character El personaje de Disney cuyo estado se desea alternar.
     */
    suspend operator fun invoke(character: Character) {
        repository.toggleFavorite(character)
    }
}
