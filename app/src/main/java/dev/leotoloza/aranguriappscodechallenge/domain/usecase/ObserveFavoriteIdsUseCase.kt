package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para observar el flujo reactivo de los identificadores únicos de personajes favoritos.
 *
 * @property repository El repositorio de personajes.
 */
class ObserveFavoriteIdsUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    /**
     * Ejecuta el caso de uso para observar los identificadores de personajes favoritos.
     *
     * @return Un [Flow] que emite el conjunto de identificadores (enteros) favoritos.
     */
    operator fun invoke(): Flow<Set<Int>> = repository.getFavoriteIds()
}
