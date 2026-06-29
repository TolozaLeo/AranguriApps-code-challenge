package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterFilter
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * Pruebas unitarias para el caso de uso [FilterCharactersUseCase].
 */
class FilterCharactersUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = FilterCharactersUseCase(repository)

    private companion object {
        const val TEST_PAGE = 1
        const val SEARCH_QUERY = "Goofy"
        const val TEST_ID = 200
        const val TEST_NAME = "Goofy"
        const val TEST_IMAGE_URL = "http://goofy.jpg"
        const val TEST_URL = "http://goofy-api"
        const val ERROR_MESSAGE = "Error de red al buscar"
    }

    private val mockCharacter = Character(
        id = TEST_ID,
        name = TEST_NAME,
        imageUrl = TEST_IMAGE_URL,
        url = TEST_URL,
        films = emptyList(),
        shortFilms = emptyList(),
        tvShows = emptyList(),
        videoGames = emptyList()
    )

    /**
     * Verifica que el caso de uso de filtrado devuelva datos exitosos al coincidir el filtro.
     */
    @Test
    fun invoke_withFilter_success_returns_success_result() = runTest {
        // Given (Dado un filtro de búsqueda y que el repositorio retorna éxito)
        val filter = CharacterFilter.ByName(SEARCH_QUERY)
        val expectedList = listOf(mockCharacter)
        coEvery { repository.filterCharacters(filter, TEST_PAGE) } returns Result.success(expectedList)

        // When (Cuando se ejecuta el filtrado)
        val result = useCase(filter, TEST_PAGE)

        // Then (Entonces el resultado es exitoso y retorna los datos esperados)
        assertTrue(result.isSuccess)
        assertEquals(expectedList, result.getOrThrow())
    }

    /**
     * Verifica que el caso de uso de filtrado devuelva fallo si el repositorio falla.
     */
    @Test
    fun invoke_withFilter_failure_returns_failure_result() = runTest {
        // Given (Dado que el repositorio devuelve un fallo de red)
        val filter = CharacterFilter.ByName(SEARCH_QUERY)
        val exception = IOException(ERROR_MESSAGE)
        coEvery { repository.filterCharacters(filter, TEST_PAGE) } returns Result.failure(exception)

        // When (Cuando se ejecuta el filtrado)
        val result = useCase(filter, TEST_PAGE)

        // Then (Entonces se propaga la excepción adecuadamente)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
