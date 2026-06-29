package dev.leotoloza.aranguriappscodechallenge.data.network.dto

import com.squareup.moshi.JsonClass

/**
 * DTO envelope para respuestas de la API que devuelven un único personaje.
 * Corresponde al endpoint `GET /character/{id}`.
 *
 * @property info Metadatos de la respuesta, incluyendo el conteo total.
 * @property data El personaje encontrado.
 */
@JsonClass(generateAdapter = true)
data class CharacterResponseDto(
    val info: InfoDto,
    val data: CharacterDto
)

/**
 * DTO envelope para respuestas de la API que devuelven una lista de personajes.
 * Corresponde a los endpoints `GET /character?page=N` y `GET /character?{filtro}=valor`.
 *
 * @property info Metadatos de paginación, incluyendo conteo y páginas.
 * @property data Lista de personajes encontrados.
 */
@JsonClass(generateAdapter = true)
data class CharactersListResponseDto(
    val info: InfoDto,
    val data: List<CharacterDto>
)
