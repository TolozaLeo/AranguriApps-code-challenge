package dev.leotoloza.aranguriappscodechallenge.domain.usecase

import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias para [ObserveFavoriteIdsUseCase].
 */
class ObserveFavoriteIdsUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = ObserveFavoriteIdsUseCase(repository)

    /**
     * Verifica que al invocar el caso de uso se obtenga el flujo correcto de los IDs de los favoritos.
     */
    @Test
    fun invoke_returns_favorite_ids_flow_from_repository() = runTest {
        // Given
        val expectedSet = setOf(1, 2, 3)
        coEvery { repository.getFavoriteIds() } returns flowOf(expectedSet)

        // When
        val result = useCase().first()

        // Then
        assertEquals(expectedSet, result)
    }
}
