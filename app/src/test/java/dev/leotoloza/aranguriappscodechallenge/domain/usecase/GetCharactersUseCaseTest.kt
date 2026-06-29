package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.PaginatedResult
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * Pruebas unitarias para el caso de uso [GetCharactersUseCase].
 */
class GetCharactersUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = GetCharactersUseCase(repository)

    private companion object {
        const val TEST_PAGE = 1
        const val TEST_ID = 100
        const val TEST_NAME = "Donald Duck"
        const val TEST_IMAGE_URL = "http://donald.jpg"
        const val TEST_URL = "http://donald-api"
        const val ERROR_MESSAGE = "Fallo de conexión"
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
     * Verifica que el caso de uso retorne el resultado paginado de personajes
     * cuando el repositorio responda satisfactoriamente.
     */
    @Test
    fun invoke_success_returns_success_result() = runTest {
        // Given (Dado que el repositorio responde con éxito con un resultado paginado)
        val expectedPaginatedResult = PaginatedResult(
            items = listOf(mockCharacter),
            hasNextPage = true
        )
        coEvery { repository.getCharacters(TEST_PAGE) } returns Result.success(expectedPaginatedResult)

        // When (Cuando se ejecuta el caso de uso)
        val result = useCase(TEST_PAGE)

        // Then (Entonces se propaga el éxito y se retornan los personajes correctos)
        assertTrue(result.isSuccess)
        val paginatedResult = result.getOrThrow()
        assertEquals(expectedPaginatedResult, paginatedResult)
        assertEquals(TEST_ID, paginatedResult.items[0].id)
        assertTrue(paginatedResult.hasNextPage)
    }

    /**
     * Verifica que el caso de uso retorne un resultado fallido cuando el repositorio
     * falla o devuelve un error.
     */
    @Test
    fun invoke_failure_returns_failure_result() = runTest {
        // Given (Dado que el repositorio falla al obtener personajes)
        val exception = IOException(ERROR_MESSAGE)
        coEvery { repository.getCharacters(TEST_PAGE) } returns Result.failure(exception)

        // When (Cuando se ejecuta el caso de uso)
        val result = useCase(TEST_PAGE)

        // Then (Entonces se propaga el fallo con la excepción esperada)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
