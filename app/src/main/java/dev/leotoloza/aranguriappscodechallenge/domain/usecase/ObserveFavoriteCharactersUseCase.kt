package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para observar el flujo reactivo de la lista de personajes marcados como favoritos.
 *
 * @property repository El repositorio de personajes.
 */
class ObserveFavoriteCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    /**
     * Ejecuta el caso de uso para observar los personajes favoritos.
     *
     * @return Un [Flow] que emite la lista actualizada de [Character] favoritos.
     */
    operator fun invoke(): Flow<List<Character>> = repository.getFavoriteCharacters()
}
