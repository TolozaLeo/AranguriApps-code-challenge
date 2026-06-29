package dev.leotoloza.aranguriappscodechallenge.domain.model

/**
 * Modelo genérico que encapsula una página de resultados junto con la señal
 * de si existen más páginas disponibles en la fuente de datos.
 *
 * Mantiene la capa de dominio libre de detalles de red (URLs, números de página),
 * exponiendo solo la información semántica que necesita el consumidor.
 *
 * @param T Tipo de los elementos contenidos en la página.
 * @property items Lista de elementos de la página actual.
 * @property hasNextPage Indica si existe al menos una página más de resultados.
 */
data class PaginatedResult<T>(
    val items: List<T>,
    val hasNextPage: Boolean
)
