package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias para [ObserveFavoriteCharactersUseCase].
 */
class ObserveFavoriteCharactersUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = ObserveFavoriteCharactersUseCase(repository)

    /**
     * Verifica que al invocar el caso de uso se obtenga el flujo correcto de favoritos.
     */
    @Test
    fun invoke_returns_favorite_characters_flow_from_repository() = runTest {
        // Given
        val expectedList = listOf(
            Character(
                id = 1,
                name = "Mickey Mouse",
                imageUrl = "",
                url = "",
                films = emptyList(),
                shortFilms = emptyList(),
                tvShows = emptyList(),
                videoGames = emptyList()
            )
        )
        coEvery { repository.getFavoriteCharacters() } returns flowOf(expectedList)

        // When
        val result = useCase().first()

        // Then
        assertEquals(expectedList, result)
    }
}
