package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import javax.inject.Inject
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterFilter
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository

/**
 * Caso de uso para filtrar personajes usando un criterio soportado por la API de Disney.
 * Acepta cualquier subclase de [CharacterFilter]: nombre, película, serie de TV,
 * cortometraje o videojuego.
 *
 * @property repository Repositorio de personajes inyectado por la capa de inyección de dependencias.
 */
class FilterCharactersUseCase @Inject constructor(private val repository: CharacterRepository) {

    /**
     * Ejecuta la búsqueda/filtrado de personajes según el criterio indicado.
     *
     * @param filter Criterio de filtrado. Ver subclases de [CharacterFilter].
     * @return [Result] con la lista de [Character] coincidentes, o el error en caso de fallo.
     */
    suspend operator fun invoke(filter: CharacterFilter, page: Int = 1): Result<List<Character>> =
        repository.filterCharacters(filter, page)
}
