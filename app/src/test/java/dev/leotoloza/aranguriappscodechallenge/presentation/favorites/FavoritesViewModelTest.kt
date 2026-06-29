package dev.leotoloza.aranguriappscodechallenge.presentation.favorites

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ObserveFavoriteCharactersUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para el [FavoritesViewModel].
 *
 * Verifica la carga de favoritos, los estados de UI (Loading, Empty, Success) y
 * la acción de alternar favoritos.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val observeFavoriteCharactersUseCase: ObserveFavoriteCharactersUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val sampleCharacter = Character(
        id = 100,
        name = "Mickey Mouse",
        imageUrl = "http://mickey.jpg",
        url = "http://mickey-api",
        films = listOf("Fantasia"),
        shortFilms = emptyList(),
        tvShows = emptyList(),
        videoGames = emptyList()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifica que el estado inicial del ViewModel sea Loading antes de procesar flujos.
     */
    @Test
    fun initialState_is_loading() = runTest {
        // Given (Dado un flujo que aún no emite nada)
        val favoritesFlow = MutableStateFlow<List<Character>>(emptyList())
        coEvery { observeFavoriteCharactersUseCase() } returns favoritesFlow

        // When (Cuando se instancia el ViewModel)
        val viewModel = FavoritesViewModel(observeFavoriteCharactersUseCase, toggleFavoriteUseCase)

        // Then (Entonces el estado de UI es Loading)
        assertEquals(FavoritesUiState.Loading, viewModel.uiState.value)
    }

    /**
     * Verifica que si no hay personajes favoritos, el estado transicione a Empty.
     */
    @Test
    fun observeFavorites_emits_empty_list_transitions_to_empty() = runTest {
        // Given (Dado un flujo que emite una lista vacía de favoritos)
        val favoritesFlow = MutableStateFlow<List<Character>>(emptyList())
        coEvery { observeFavoriteCharactersUseCase() } returns favoritesFlow

        val viewModel = FavoritesViewModel(observeFavoriteCharactersUseCase, toggleFavoriteUseCase)

        // When (Cuando avanza la corrutina)
        advanceUntilIdle()

        // Then (Entonces el estado de UI es Empty)
        assertEquals(FavoritesUiState.Empty, viewModel.uiState.value)
    }

    /**
     * Verifica que si hay personajes favoritos, el estado transicione a Success.
     */
    @Test
    fun observeFavorites_emits_characters_transitions_to_success() = runTest {
        // Given (Dado un flujo que emite una lista con personajes)
        val favoritesFlow = MutableStateFlow<List<Character>>(emptyList())
        coEvery { observeFavoriteCharactersUseCase() } returns favoritesFlow

        val viewModel = FavoritesViewModel(observeFavoriteCharactersUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // When (Cuando el flujo emite personajes)
        favoritesFlow.value = listOf(sampleCharacter)
        advanceUntilIdle()

        // Then (Entonces el estado de UI es Success con los personajes correspondientes)
        val state = viewModel.uiState.value
        assertTrue(state is FavoritesUiState.Success)
        assertEquals(1, (state as FavoritesUiState.Success).characters.size)
        assertEquals("Mickey Mouse", state.characters[0].name)
    }

    /**
     * Verifica que llamar a toggleFavorite invoque el caso de uso correspondiente.
     */
    @Test
    fun toggleFavorite_invokes_usecase() = runTest {
        // Given (Dado que se tiene el flujo de favoritos configurado)
        val favoritesFlow = MutableStateFlow<List<Character>>(emptyList())
        coEvery { observeFavoriteCharactersUseCase() } returns favoritesFlow
        coEvery { toggleFavoriteUseCase(sampleCharacter) } returns Unit

        val viewModel = FavoritesViewModel(observeFavoriteCharactersUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // When (Cuando se solicita alternar el estado de favorito)
        viewModel.toggleFavorite(sampleCharacter)
        advanceUntilIdle()

        // Then (Entonces se ejecuta el caso de uso)
        coVerify { toggleFavoriteUseCase(sampleCharacter) }
    }
}
