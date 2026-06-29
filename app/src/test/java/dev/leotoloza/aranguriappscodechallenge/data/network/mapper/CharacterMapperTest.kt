package dev.leotoloza.aranguriappscodechallenge.data.network.mapper

import dev.leotoloza.aranguriappscodechallenge.data.network.dto.CharacterDto
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias para verificar el funcionamiento del mapeador [CharacterMapper].
 */
class CharacterMapperTest {

    private companion object {
        const val TEST_ID = 308
        const val TEST_NAME = "Queen Arianna"
        const val TEST_IMAGE_URL = "https://static.wikia.nocookie.net/disney/images/1/15/Arianna_Tangled.jpg"
        const val TEST_URL = "https://api.disneyapi.dev/characters/308"
        const val TEST_FILM = "Tangled"
        const val TEST_SHORT_FILM = "Tangled Ever After"
        const val TEST_TV_SHOW = "Tangled: The Series"
        const val TEST_VIDEO_GAME = "Kingdom Hearts III"
    }

    /**
     * Verifica que [toDomain] mapea correctamente todos los campos de un [CharacterDto] válido
     * hacia un objeto de dominio.
     */
    @Test
    fun toDomain_maps_valid_dto_fields_correctly() {
        // Given (Dado un DTO de personaje con valores correctos)
        val dto = CharacterDto(
            id = TEST_ID,
            name = TEST_NAME,
            imageUrl = TEST_IMAGE_URL,
            url = TEST_URL,
            films = listOf(TEST_FILM),
            shortFilms = listOf(TEST_SHORT_FILM),
            tvShows = listOf(TEST_TV_SHOW),
            videoGames = listOf(TEST_VIDEO_GAME)
        )

        // When (Cuando se realiza el mapeo)
        val domain = dto.toDomain()

        // Then (Entonces se comprueba la equivalencia de los campos mapeados)
        assertEquals(TEST_ID, domain.id)
        assertEquals(TEST_NAME, domain.name)
        assertEquals(TEST_IMAGE_URL, domain.imageUrl)
        assertEquals(TEST_URL, domain.url)
        assertEquals(listOf(TEST_FILM), domain.films)
        assertEquals(listOf(TEST_SHORT_FILM), domain.shortFilms)
        assertEquals(listOf(TEST_TV_SHOW), domain.tvShows)
        assertEquals(listOf(TEST_VIDEO_GAME), domain.videoGames)
    }

    /**
     * Verifica que [toDomain] maneja correctamente listas vacías de apariciones
     * y las mapea como tales hacia el modelo de dominio.
     */
    @Test
    fun toDomain_handles_empty_lists_correctly() {
        // Given (Dado un DTO de personaje con listas de apariciones vacías)
        val dto = CharacterDto(
            id = TEST_ID,
            name = TEST_NAME,
            imageUrl = TEST_IMAGE_URL,
            url = TEST_URL,
            films = emptyList(),
            shortFilms = emptyList(),
            tvShows = emptyList(),
            videoGames = emptyList()
        )

        // When (Cuando se realiza el mapeo)
        val domain = dto.toDomain()

        // Then (Entonces las listas en el dominio siguen estando vacías)
        assertEquals(emptyList<String>(), domain.films)
        assertEquals(emptyList<String>(), domain.shortFilms)
        assertEquals(emptyList<String>(), domain.tvShows)
        assertEquals(emptyList<String>(), domain.videoGames)
    }
}
