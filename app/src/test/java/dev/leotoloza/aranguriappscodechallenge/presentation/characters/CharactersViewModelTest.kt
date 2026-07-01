package dev.leotoloza.aranguriappscodechallenge.presentation.characters

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.PaginatedResult
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterFilter
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterCategory
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.GetCharactersUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ObserveFavoriteCharactersUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ObserveFavoriteIdsUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ToggleFavoriteUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.FilterCharactersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

/**
 * Pruebas unitarias para [CharactersViewModel].
 *
 * Verifica la carga inicial, la paginación, el manejo de errores, la función de reintentar
 * y la actualización reactiva de los favoritos.
 * Utiliza [StandardTestDispatcher] para controlar la ejecución de coroutines.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {

    private val getCharactersUseCase: GetCharactersUseCase = mockk()
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val observeFavoriteCharactersUseCase: ObserveFavoriteCharactersUseCase = mockk()
    private val filterCharactersUseCase: FilterCharactersUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private companion object {
        const val FIRST_PAGE = 1
        const val SECOND_PAGE = 2

        const val FIRST_CHARACTER_ID = 100
        const val FIRST_CHARACTER_NAME = "Mickey Mouse"
        const val FIRST_CHARACTER_IMAGE = "http://mickey.jpg"
        const val FIRST_CHARACTER_URL = "http://mickey-api"

        const val SECOND_CHARACTER_ID = 200
        const val SECOND_CHARACTER_NAME = "Donald Duck"
        const val SECOND_CHARACTER_IMAGE = "http://donald.jpg"
        const val SECOND_CHARACTER_URL = "http://donald-api"

        const val ERROR_MESSAGE = "Fallo de conexión"
    }

    private val firstPageCharacter = Character(
        id = FIRST_CHARACTER_ID,
        name = FIRST_CHARACTER_NAME,
        imageUrl = FIRST_CHARACTER_IMAGE,
        url = FIRST_CHARACTER_URL,
        films = listOf("Fantasia"),
        shortFilms = emptyList(),
        tvShows = emptyList(),
        videoGames = emptyList()
    )

    private val secondPageCharacter = Character(
        id = SECOND_CHARACTER_ID,
        name = SECOND_CHARACTER_NAME,
        imageUrl = SECOND_CHARACTER_IMAGE,
        url = SECOND_CHARACTER_URL,
        films = emptyList(),
        shortFilms = emptyList(),
        tvShows = listOf("DuckTales"),
        videoGames = emptyList()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { observeFavoriteIdsUseCase() } returns flowOf(emptySet())
        coEvery { observeFavoriteCharactersUseCase() } returns flowOf(emptyList())
        coEvery { filterCharactersUseCase(any(), any()) } returns Result.success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifica que el estado inicial sea Loading y transite a Success
     * tras una carga exitosa de la primera página.
     */
    @Test
    fun initialState_is_loading_then_transitions_to_success() = runTest {
        // Given (Dado que la primera página retorna personajes con éxito)
        val paginatedResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(paginatedResult)

        // When (Cuando se crea el ViewModel)
        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)

        // Then (Entonces el estado inicial es Loading)
        assertEquals(CharactersUiState.Loading, viewModel.uiState.value)

        // When (Cuando se completa la coroutine)
        advanceUntilIdle()

        // Then (Entonces transiciona a Success con los personajes correctos)
        val state = viewModel.uiState.value
        assertTrue(state is CharactersUiState.Success)
        val successState = state as CharactersUiState.Success
        assertEquals(1, successState.characters.size)
        assertEquals(FIRST_CHARACTER_NAME, successState.characters[0].name)
        assertFalse(successState.isLoadingNextPage)
        assertTrue(successState.hasNextPage)
    }

    /**
     * Verifica que la paginación acumule los personajes de ambas páginas
     * en una única lista combinada.
     */
    @Test
    fun loadNextPage_accumulates_characters_from_multiple_pages() = runTest {
        // Given (Dado que la primera y segunda página retornan personajes distintos)
        val firstPageResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        val secondPageResult = PaginatedResult(
            items = listOf(secondPageCharacter),
            hasNextPage = false
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(firstPageResult)
        coEvery { getCharactersUseCase(SECOND_PAGE) } returns Result.success(secondPageResult)

        // When (Cuando se carga la primera página)
        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)
        advanceUntilIdle()

        // When (Cuando se solicita la segunda página)
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then (Entonces la lista contiene los personajes de ambas páginas acumulados)
        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(2, state.characters.size)
        assertEquals(FIRST_CHARACTER_NAME, state.characters[0].name)
        assertEquals(SECOND_CHARACTER_NAME, state.characters[1].name)
        assertFalse(state.hasNextPage)
    }

    /**
     * Verifica que no se ejecute una nueva carga cuando hasNextPage es false,
     * previniendo solicitudes innecesarias a la API.
     */
    @Test
    fun loadNextPage_does_not_load_when_no_more_pages() = runTest {
        // Given (Dado que la primera página indica que no hay más páginas)
        val lastPageResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = false
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(lastPageResult)

        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)
        advanceUntilIdle()

        // When (Cuando se intenta cargar la siguiente página)
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then (Entonces la lista no cambia — solo tiene un personaje de la primera carga)
        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(1, state.characters.size)
        assertFalse(state.hasNextPage)
    }

    /**
     * Verifica que un error de red (UnknownHostException) en la carga inicial
     * produzca un estado Error con el mensaje de falta de conexión a internet.
     */
    @Test
    fun initialLoad_networkError_produces_error_state_with_noInternet_message() = runTest {
        // Given (Dado que la primera carga falla por falta de conexión)
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.failure(UnknownHostException())

        // When (Cuando se crea el ViewModel y se completa la coroutine)
        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)
        advanceUntilIdle()

        // Then (Entonces el estado es Error con mensaje de sin conexión)
        val state = viewModel.uiState.value
        assertTrue(state is CharactersUiState.Error)
        assertEquals(
            CharactersViewModel.ERROR_MESSAGE_NO_INTERNET,
            (state as CharactersUiState.Error).message
        )
    }

    /**
     * Verifica que un error genérico (no de red) en la carga inicial
     * produzca un estado Error con el mensaje genérico de error.
     */
    @Test
    fun initialLoad_genericError_produces_error_state_with_generic_message() = runTest {
        // Given (Dado que la primera carga falla con un error genérico)
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.failure(Exception(ERROR_MESSAGE))

        // When (Cuando se crea el ViewModel y se completa la coroutine)
        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)
        advanceUntilIdle()

        // Then (Entonces el estado es Error con mensaje genérico)
        val state = viewModel.uiState.value
        assertTrue(state is CharactersUiState.Error)
        assertEquals(
            CharactersViewModel.ERROR_MESSAGE_GENERIC,
            (state as CharactersUiState.Error).message
        )
    }

    /**
     * Verifica que la función retry() resetee el estado y recargue
     * exitosamente desde la primera página.
     */
    @Test
    fun retry_resets_state_and_reloads_from_first_page() = runTest {
        // Given (Dado que la primera carga falla)
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.failure(Exception(ERROR_MESSAGE))

        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)
        advanceUntilIdle()

        // Verificar que estamos en estado de error
        assertTrue(viewModel.uiState.value is CharactersUiState.Error)

        // Given (Dado que ahora la API responde correctamente)
        val successResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(successResult)

        // When (Cuando se ejecuta retry)
        viewModel.retry()
        advanceUntilIdle()

        // Then (Entonces transiciona a Success con los personajes correctos)
        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(1, state.characters.size)
        assertEquals(FIRST_CHARACTER_NAME, state.characters[0].name)
    }

    /**
     * Verifica que un error en la carga de una página posterior (no la primera)
     * preserve los datos existentes sin mostrar pantalla de error completa.
     */
    @Test
    fun loadNextPage_error_preserves_existing_data() = runTest {
        // Given (Dado que la primera página carga exitosamente)
        val firstPageResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(firstPageResult)

        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)
        advanceUntilIdle()

        // Given (Dado que la segunda página falla)
        coEvery { getCharactersUseCase(SECOND_PAGE) } returns Result.failure(Exception(ERROR_MESSAGE))

        // When (Cuando se intenta cargar la siguiente página)
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then (Entonces se mantiene el estado Success con los datos existentes)
        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(1, state.characters.size)
        assertEquals(FIRST_CHARACTER_NAME, state.characters[0].name)
        assertFalse(state.isLoadingNextPage)
        assertEquals(CharactersViewModel.ERROR_MESSAGE_GENERIC, state.pagingError)
    }

    /**
     * Verifica que un error genérico en la carga de una página posterior
     * establezca la propiedad pagingError con el mensaje genérico.
     */
    @Test
    fun loadNextPage_genericError_sets_pagingError_with_generic_message() = runTest {
        // Given (Dado que la primera página carga exitosamente)
        val firstPageResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(firstPageResult)

        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        // Given (Dado que la segunda página falla con error genérico)
        coEvery { getCharactersUseCase(SECOND_PAGE) } returns Result.failure(Exception(ERROR_MESSAGE))

        // When (Cuando se intenta cargar la siguiente página)
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then (Entonces el estado Success contiene el mensaje de error genérico)
        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(CharactersViewModel.ERROR_MESSAGE_GENERIC, state.pagingError)
    }

    /**
     * Verifica que un error de red (UnknownHostException) en la carga de una página posterior
     * establezca la propiedad pagingError con el mensaje de falta de conexión a internet.
     */
    @Test
    fun loadNextPage_networkError_sets_pagingError_with_noInternet_message() = runTest {
        // Given (Dado que la primera página carga exitosamente)
        val firstPageResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(firstPageResult)

        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        // Given (Dado que la segunda página falla por falta de conexión)
        coEvery { getCharactersUseCase(SECOND_PAGE) } returns Result.failure(UnknownHostException())

        // When (Cuando se intenta cargar la siguiente página)
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then (Entonces el estado Success contiene el mensaje de sin conexión)
        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(CharactersViewModel.ERROR_MESSAGE_NO_INTERNET, state.pagingError)
    }

    /**
     * Verifica que la llamada a clearPagingError() limpie el error de paginación
     * restableciéndolo a null.
     */
    @Test
    fun clearPagingError_resets_pagingError_to_null() = runTest {
        // Given (Dado que la primera página carga exitosamente y la segunda falla)
        val firstPageResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(firstPageResult)

        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        coEvery { getCharactersUseCase(SECOND_PAGE) } returns Result.failure(Exception(ERROR_MESSAGE))
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Verificar que el error no sea nulo inicialmente
        val stateWithError = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(CharactersViewModel.ERROR_MESSAGE_GENERIC, stateWithError.pagingError)

        // When (Cuando se limpia el error de paginación)
        viewModel.clearPagingError()

        // Then (Entonces la propiedad pagingError en el estado es null)
        val stateCleared = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(null, stateCleared.pagingError)
    }

    /**
     * Verifica que al emitir nuevos IDs favoritos el estado de éxito se actualice reactivamente.
     */
    @Test
    fun observeFavorites_updates_success_state_reactively() = runTest {
        // Given (Dado que se carga la primera página con éxito)
        val paginatedResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(paginatedResult)

        val favoriteIdsFlow = MutableStateFlow(emptySet<Int>())
        coEvery { observeFavoriteIdsUseCase() } returns favoriteIdsFlow

        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)
        advanceUntilIdle()

        // Verificar que el estado inicial no tenga favoritos
        val successStateBefore = viewModel.uiState.value as CharactersUiState.Success
        assertTrue(successStateBefore.favoriteIds.isEmpty())

        // When (Cuando se emite un ID favorito)
        favoriteIdsFlow.value = setOf(FIRST_CHARACTER_ID)
        advanceUntilIdle()

        // Then (Entonces el estado de UI refleja el nuevo favorito)
        val successStateAfter = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(setOf(FIRST_CHARACTER_ID), successStateAfter.favoriteIds)
    }

    /**
     * Verifica que la llamada a toggleFavorite invoque el caso de uso correspondiente.
     */
    @Test
    fun toggleFavorite_invokes_usecase() = runTest {
        // Given (Dado que se carga la primera página con éxito)
        val paginatedResult = PaginatedResult(
            items = listOf(firstPageCharacter),
            hasNextPage = true
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(paginatedResult)
        coEvery { toggleFavoriteUseCase(firstPageCharacter) } returns Unit

        val viewModel = CharactersViewModel(getCharactersUseCase, observeFavoriteIdsUseCase, toggleFavoriteUseCase, observeFavoriteCharactersUseCase, filterCharactersUseCase)
        advanceUntilIdle()

        // When (Cuando se solicita alternar favorito)
        viewModel.toggleFavorite(firstPageCharacter)
        advanceUntilIdle()

        // Then (Entonces se invoca el caso de uso correspondiente)
        coVerify { toggleFavoriteUseCase(firstPageCharacter) }
    }

    /**
     * Verifica que si la carga inicial de la API falla pero existen favoritos locales,
     * se muestren los favoritos locales en estado de éxito (fallback offline).
     */
    @Test
    fun initialLoad_networkError_withLocalFavorites_loadsLocalFavoritesAsFallback() = runTest {
        // Given (Dado que la llamada a la API falla y hay favoritos locales)
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.failure(UnknownHostException())
        coEvery { observeFavoriteCharactersUseCase() } returns flowOf(listOf(firstPageCharacter))

        // When (Cuando se crea el ViewModel)
        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        // Then (Entonces el estado final debe ser Success con los favoritos locales)
        val state = viewModel.uiState.value
        assertTrue(state is CharactersUiState.Success)
        val successState = state as CharactersUiState.Success
        assertEquals(1, successState.characters.size)
        assertEquals(FIRST_CHARACTER_NAME, successState.characters[0].name)
        assertFalse(successState.hasNextPage)
    }

    /**
     * Verifica que si la carga inicial de la API falla y NO existen favoritos locales,
     * se muestre el estado de error normal.
     */
    @Test
    fun initialLoad_networkError_withoutLocalFavorites_showsErrorState() = runTest {
        // Given (Dado que la llamada a la API falla y no hay favoritos locales)
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.failure(UnknownHostException())
        coEvery { observeFavoriteCharactersUseCase() } returns flowOf(emptyList())

        // When (Cuando se crea el ViewModel)
        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        // Then (Entonces el estado final es Error)
        val state = viewModel.uiState.value
        assertTrue(state is CharactersUiState.Error)
        assertEquals(
            CharactersViewModel.ERROR_MESSAGE_NO_INTERNET,
            (state as CharactersUiState.Error).message
        )
    }

    /**
     * Verifica que al llamar a searchCharacters("Mickey") se invoque a filterCharactersUseCase
     * en lugar de getCharactersUseCase, reiniciando la página a 1 y actualizando la lista de personajes.
     */
    @Test
    fun searchCharacters_triggers_search_on_api_correctly() = runTest {
        // Given (Dado que la carga inicial de personajes retorna lista vacía)
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(PaginatedResult(emptyList(), false))

        // Dado que la primera página de búsqueda retorna personajes con éxito)
        val searchResult = listOf(firstPageCharacter)
        coEvery { filterCharactersUseCase(CharacterFilter.ByName("Mickey"), FIRST_PAGE) } returns Result.success(searchResult)

        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        // When (Cuando se ejecuta la búsqueda)
        viewModel.searchCharacters("Mickey")
        advanceUntilIdle()

        // Then (Entonces el estado Success contiene la lista filtrada de la API)
        val state = viewModel.uiState.value
        assertTrue(state is CharactersUiState.Success)
        val successState = state as CharactersUiState.Success
        assertEquals(1, successState.characters.size)
        assertEquals("Mickey Mouse", successState.characters[0].name)
        assertEquals("Mickey", successState.searchQuery)
        coVerify { filterCharactersUseCase(CharacterFilter.ByName("Mickey"), FIRST_PAGE) }
    }

    /**
     * Verifica que el scroll infinito acumule los siguientes personajes filtrados de la página 2
     * si se vuelve a llamar a loadNextPage() mientras existe una búsqueda activa.
     */
    @Test
    fun searchCharacters_paging_loads_next_filtered_page() = runTest {
        // Given (Dado que la carga inicial de personajes retorna lista vacía)
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(PaginatedResult(emptyList(), false))

        // Dado que se tiene una búsqueda activa de la primera página y la segunda página también tiene resultados)
        val firstPageSearchResult = List(50) { firstPageCharacter }
        val secondPageSearchResult = listOf(secondPageCharacter)
        coEvery { filterCharactersUseCase(CharacterFilter.ByName("Mickey"), FIRST_PAGE) } returns Result.success(firstPageSearchResult)
        coEvery { filterCharactersUseCase(CharacterFilter.ByName("Mickey"), SECOND_PAGE) } returns Result.success(secondPageSearchResult)

        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        viewModel.searchCharacters("Mickey")
        advanceUntilIdle()

        // When (Cuando se carga la siguiente página de búsqueda)
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then (Entonces se acumulan los resultados de búsqueda de la página 2)
        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(51, state.characters.size)
        assertEquals(SECOND_CHARACTER_NAME, state.characters[50].name)
    }

    /**
     * Verifica que al vaciar la búsqueda se reinicie la paginación y se recargue la lista de personajes general.
     */
    @Test
    fun clearSearch_restores_unfiltered_paginated_list() = runTest {
        // Given (Dado que hay una búsqueda activa cargada)
        val searchResult = listOf(firstPageCharacter)
        coEvery { filterCharactersUseCase(CharacterFilter.ByName("Mickey"), FIRST_PAGE) } returns Result.success(searchResult)
        
        val defaultResult = PaginatedResult(items = listOf(secondPageCharacter), hasNextPage = false)
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(defaultResult)

        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        viewModel.searchCharacters("Mickey")
        advanceUntilIdle()

        // When (Cuando se limpia la búsqueda)
        viewModel.clearSearch()
        advanceUntilIdle()

        // Then (Entonces se limpia la query del estado y se vuelven a cargar los personajes generales)
        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(1, state.characters.size)
        assertEquals(SECOND_CHARACTER_NAME, state.characters[0].name)
        assertEquals("", state.searchQuery)
    }

    /**
     * Verifica que al seleccionar una categoría, los personajes expuestos en [CharactersUiState.Success]
     * se filtren correctamente en memoria en el ViewModel.
     */
    @Test
    fun selectCategory_filters_characters_in_memory_correctly() = runTest {
        // Given (Dado que la primera página retorna dos personajes de diferentes categorías)
        val paginatedResult = PaginatedResult(
            items = listOf(firstPageCharacter, secondPageCharacter),
            hasNextPage = false
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(paginatedResult)

        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        // When (Cuando se selecciona la categoría FILM)
        viewModel.selectCategory(CharacterCategory.FILM)
        advanceUntilIdle()

        // Then (Entonces la lista filtrada solo contiene a firstPageCharacter)
        val state = viewModel.uiState.value
        assertTrue(state is CharactersUiState.Success)
        val successState = state as CharactersUiState.Success
        assertEquals(1, successState.characters.size)
        assertEquals(FIRST_CHARACTER_NAME, successState.characters[0].name)
        assertEquals(CharacterCategory.FILM, successState.selectedCategory)
    }

    /**
     * Verifica que al seleccionar null como categoría, se vuelvan a mostrar todos los personajes cargados.
     */
    @Test
    fun selectCategory_restores_all_characters_when_null() = runTest {
        val paginatedResult = PaginatedResult(
            items = listOf(firstPageCharacter, secondPageCharacter),
            hasNextPage = false
        )
        coEvery { getCharactersUseCase(FIRST_PAGE) } returns Result.success(paginatedResult)

        val viewModel = CharactersViewModel(
            getCharactersUseCase,
            observeFavoriteIdsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteCharactersUseCase,
            filterCharactersUseCase
        )
        advanceUntilIdle()

        // Seleccionar categoría y luego limpiarla
        viewModel.selectCategory(CharacterCategory.FILM)
        advanceUntilIdle()
        viewModel.selectCategory(null)
        advanceUntilIdle()

        val state = viewModel.uiState.value as CharactersUiState.Success
        assertEquals(2, state.characters.size)
        assertEquals(null, state.selectedCategory)
    }
}
