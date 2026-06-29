package dev.leotoloza.aranguriappscodechallenge.domain.repository

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterFilter
import dev.leotoloza.aranguriappscodechallenge.domain.model.PaginatedResult

/**
 * Contrato del repositorio de personajes.
 * Define las operaciones de acceso a datos disponibles para la capa de dominio.
 * La implementación concreta reside en la capa `data`.
 */
interface CharacterRepository {

    /**
     * Obtiene una página de personajes desde la fuente de datos remota.
     *
     * @param page Número de página a recuperar (basado en 1).
     * @return [Result] con [PaginatedResult] conteniendo la lista de [Character]
     *         y la señal de paginación, o el error en caso de fallo.
     */
    suspend fun getCharacters(page: Int): Result<PaginatedResult<Character>>

    /**
     * Busca personajes aplicando un criterio de filtrado soportado por la API.
     * Cada subclase de [CharacterFilter] se traduce a un query param distinto
     * en el endpoint remoto `GET /character`.
     *
     * @param filter Criterio de búsqueda (nombre, película, serie, cortometraje o videojuego).
     * @return [Result] con la lista de [Character] coincidentes.
     */
    suspend fun filterCharacters(filter: CharacterFilter, page: Int = 1): Result<List<Character>>

    /**
     * Obtiene el detalle completo de un personaje específico.
     *
     * @param id Identificador único del personaje.
     * @return [Result] con el [Character] encontrado, o error si no existe.
     */
    suspend fun getCharacterById(id: Int): Result<Character>
}
