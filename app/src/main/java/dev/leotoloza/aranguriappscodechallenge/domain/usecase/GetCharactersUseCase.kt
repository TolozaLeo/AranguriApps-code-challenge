package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.PaginatedResult
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener una página paginada de personajes desde la fuente remota.
 * Delega directamente en [CharacterRepository].
 *
 * @property repository Repositorio de personajes inyectado por la capa de inyección de dependencias.
 */
class GetCharactersUseCase @Inject constructor(private val repository: CharacterRepository) {

    /**
     * Ejecuta la obtención de una página de personajes.
     *
     * @param page Número de página a recuperar (basado en 1).
     * @return [Result] con [PaginatedResult] conteniendo la lista de [Character]
     *         y la señal de paginación, o el error en caso de fallo.
     */
    suspend operator fun invoke(page: Int): Result<PaginatedResult<Character>> =
        repository.getCharacters(page)
}
