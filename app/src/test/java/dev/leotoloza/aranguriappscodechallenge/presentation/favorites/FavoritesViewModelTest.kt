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

    /**
     * Verifica que al buscar un término, la lista de favoritos expuesta en [FavoritesUiState.Success]
     * se filtre correctamente sin modificar el origen de datos.
     */
    @Test
    fun searchCharacters_filters_favorites_in_memory_correctly() = runTest {
        // Given (Dado un flujo con múltiples personajes favoritos)
        val mickey = sampleCharacter
        val donald = Character(
            id = 200,
            name = "Donald Duck",
            imageUrl = "http://donald.jpg",
            url = "http://donald-api",
            films = emptyList(),
            shortFilms = emptyList(),
            tvShows = emptyList(),
            videoGames = emptyList()
        )
        val favoritesFlow = MutableStateFlow(listOf(mickey, donald))
        coEvery { observeFavoriteCharactersUseCase() } returns favoritesFlow

        val viewModel = FavoritesViewModel(observeFavoriteCharactersUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // When (Cuando se busca "Donald")
        viewModel.searchCharacters("Donald")
        advanceUntilIdle()

        // Then (Entonces la lista filtrada solo contiene a Donald Duck)
        val state = viewModel.uiState.value
        assertTrue(state is FavoritesUiState.Success)
        val successState = state as FavoritesUiState.Success
        assertEquals(1, successState.characters.size)
        assertEquals("Donald Duck", successState.characters[0].name)
        assertEquals("Donald", successState.searchQuery)
    }

    /**
     * Verifica que si ningún favorito coincide con la búsqueda, se emita un estado [FavoritesUiState.Success]
     * con la lista de personajes vacía y la query correspondiente.
     */
    @Test
    fun searchCharacters_emptyResult_emits_success_state_with_empty_list() = runTest {
        // Given (Dado que hay favoritos en la lista)
        val favoritesFlow = MutableStateFlow(listOf(sampleCharacter))
        coEvery { observeFavoriteCharactersUseCase() } returns favoritesFlow

        val viewModel = FavoritesViewModel(observeFavoriteCharactersUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // When (Cuando se busca un término que no coincide como "Goofy")
        viewModel.searchCharacters("Goofy")
        advanceUntilIdle()

        // Then (Entonces el estado sigue siendo Success pero con lista vacía y query Goofy)
        val state = viewModel.uiState.value
        assertTrue(state is FavoritesUiState.Success)
        val successState = state as FavoritesUiState.Success
        assertTrue(successState.characters.isEmpty())
        assertEquals("Goofy", successState.searchQuery)
    }

    /**
     * Verifica que al limpiar la búsqueda de favoritos se vuelva a emitir la lista completa de favoritos.
     */
    @Test
    fun clearSearch_restores_all_favorites() = runTest {
        // Given (Dado que hay favoritos y una búsqueda activa que los filtra)
        val favoritesFlow = MutableStateFlow(listOf(sampleCharacter))
        coEvery { observeFavoriteCharactersUseCase() } returns favoritesFlow

        val viewModel = FavoritesViewModel(observeFavoriteCharactersUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        viewModel.searchCharacters("Goofy")
        advanceUntilIdle()
        
        // Verificar que la lista esté vacía debido a la búsqueda
        val successStateBefore = viewModel.uiState.value as FavoritesUiState.Success
        assertTrue(successStateBefore.characters.isEmpty())

        // When (Cuando se limpia la búsqueda)
        viewModel.clearSearch()
        advanceUntilIdle()

        // Then (Entonces la lista de favoritos vuelve a mostrar todos los elementos originales)
        val successStateAfter = viewModel.uiState.value as FavoritesUiState.Success
        assertEquals(1, successStateAfter.characters.size)
        assertEquals("Mickey Mouse", successStateAfter.characters[0].name)
        assertEquals("", successStateAfter.searchQuery)
    }
}
