package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Pruebas unitarias para [ToggleFavoriteUseCase].
 */
class ToggleFavoriteUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = ToggleFavoriteUseCase(repository)

    private val sampleCharacter = Character(
        id = 1,
        name = "Mickey Mouse",
        imageUrl = "",
        url = "",
        films = emptyList(),
        shortFilms = emptyList(),
        tvShows = emptyList(),
        videoGames = emptyList()
    )

    /**
     * Verifica que al invocar el caso de uso se delegue la llamada en el repositorio.
     */
    @Test
    fun invoke_calls_toggleFavorite_on_repository() = runTest {
        // Given
        coEvery { repository.toggleFavorite(sampleCharacter) } returns Unit

        // When
        useCase(sampleCharacter)

        // Then
        coVerify { repository.toggleFavorite(sampleCharacter) }
    }
}
