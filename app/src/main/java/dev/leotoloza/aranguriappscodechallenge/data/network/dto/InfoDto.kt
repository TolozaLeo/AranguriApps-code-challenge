package dev.leotoloza.aranguriappscodechallenge.data.network.dto

import com.squareup.moshi.JsonClass

/**
 * DTO que representa el objeto `info` presente en todas las respuestas
 * del endpoint de la API de Disney.
 *
 * @property count Total de registros que coinciden con la consulta.
 * @property totalPages Total de páginas disponibles para la consulta (puede ser nulo).
 * @property previousPage URL de la página anterior (nula si no existe).
 * @property nextPage URL de la página siguiente (nula si no existe).
 */
@JsonClass(generateAdapter = true)
data class InfoDto(
    val count: Int,
    val totalPages: Int?,
    val previousPage: String?,
    val nextPage: String?
)
