package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository

/**
 * Caso de uso para obtener el detalle completo de un personaje específico por su ID.
 *
 * @property repository Repositorio de personajes inyectado por la capa de inyección de dependencias.
 */
class GetCharacterDetailUseCase(private val repository: CharacterRepository) {

    /**
     * Ejecuta la obtención del detalle de un personaje.
     *
     * @param id Identificador único del personaje.
     * @return [Result] con el [Character] encontrado, o error si no existe o falla la red.
     */
    suspend operator fun invoke(id: Int): Result<Character> =
        repository.getCharacterById(id)
}
