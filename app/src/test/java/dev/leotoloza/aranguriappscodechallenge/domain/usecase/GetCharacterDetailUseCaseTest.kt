package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * Pruebas unitarias para el caso de uso [GetCharacterDetailUseCase].
 */
class GetCharacterDetailUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = GetCharacterDetailUseCase(repository)

    private companion object {
        const val TEST_ID = 300
        const val TEST_NAME = "Minnie Mouse"
        const val TEST_IMAGE_URL = "http://minnie.jpg"
        const val TEST_URL = "http://minnie-api"
        const val ERROR_MESSAGE = "Detalle no disponible"
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
     * Verifica que el caso de uso retorne la información detallada del personaje
     * cuando el repositorio responda de forma exitosa.
     */
    @Test
    fun invoke_withId_success_returns_character_details() = runTest {
        // Given (Dado que el repositorio retorna el personaje por ID)
        coEvery { repository.getCharacterById(TEST_ID) } returns Result.success(mockCharacter)

        // When (Cuando se ejecuta el caso de uso de detalle)
        val result = useCase(TEST_ID)

        // Then (Entonces se propaga el éxito y se retornan los detalles correctos)
        assertTrue(result.isSuccess)
        val character = result.getOrThrow()
        assertEquals(TEST_ID, character.id)
        assertEquals(TEST_NAME, character.name)
    }

    /**
     * Verifica que el caso de uso retorne un fallo cuando el repositorio arroja
     * o propaga un error.
     */
    @Test
    fun invoke_withId_failure_returns_failure_result() = runTest {
        // Given (Dado que el repositorio falla al buscar el personaje)
        val exception = IOException(ERROR_MESSAGE)
        coEvery { repository.getCharacterById(TEST_ID) } returns Result.failure(exception)

        // When (Cuando se ejecuta el caso de uso)
        val result = useCase(TEST_ID)

        // Then (Entonces se propaga el fallo de la llamada)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
