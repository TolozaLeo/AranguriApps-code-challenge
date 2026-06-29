package dev.leotoloza.aranguriappscodechallenge.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.PaginatedResult
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.GetCharactersUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ObserveFavoriteCharactersUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ObserveFavoriteIdsUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * ViewModel que gestiona el estado de la pantalla de personajes.
 *
 * Se encarga de la carga inicial, la paginación (scroll infinito)
 * y el manejo de errores de red. Expone un [StateFlow] reactivo
 * de [CharactersUiState] que la UI observa para renderizar el estado correcto.
 *
 * @property getCharactersUseCase Caso de uso para obtener páginas de personajes.
 * @property observeFavoriteIdsUseCase Caso de uso para observar los identificadores de favoritos.
 * @property toggleFavoriteUseCase Caso de uso para alternar el estado de favorito de un personaje.
 * @property observeFavoriteCharactersUseCase Caso de uso para obtener la lista de personajes favoritos guardados localmente.
 */
@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val observeFavoriteCharactersUseCase: ObserveFavoriteCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharactersUiState>(CharactersUiState.Loading)

    /** Estado reactivo de la pantalla, observable desde la capa de UI. */
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var currentPage = INITIAL_PAGE
    private var hasNextPage = true
    private var isLoadingPage = false
    private var favoriteIds: Set<Int> = emptySet()

    init {
        loadNextPage()
        observeFavorites()
    }

    /**
     * Inicia la observación del conjunto de IDs de personajes favoritos.
     * Mantiene actualizado el estado de UI reactivamente sin recargar la lista de la API.
     */
    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { ids ->
                favoriteIds = ids
                val currentState = _uiState.value
                if (currentState is CharactersUiState.Success) {
                    _uiState.value = currentState.copy(favoriteIds = ids)
                }
            }
        }
    }

    /**
     * Alterna el estado de favorito de un personaje delegando en el caso de uso.
     *
     * @param character Personaje de Disney a agregar o quitar de favoritos.
     */
    fun toggleFavorite(character: Character) {
        viewModelScope.launch {
            toggleFavoriteUseCase(character)
        }
    }

    /**
     * Carga la siguiente página de personajes desde la API.
     *
     * Verifica que no se esté cargando otra página y que existan más páginas disponibles
     * antes de realizar la llamada. En caso de éxito, acumula los nuevos personajes a la
     * lista existente. En caso de error, distingue entre errores de red (sin conexión)
     * y errores genéricos del servidor.
     */
    fun loadNextPage() {
        if (isLoadingPage || !hasNextPage) return

        isLoadingPage = true
        // Mostrar indicador de paginación si ya hay datos cargados
        updateLoadingState()

        viewModelScope.launch {
            getCharactersUseCase(currentPage)
                .onSuccess { paginatedResult ->
                    handleSuccess(paginatedResult)
                }
                .onFailure { error ->
                    handleError(error)
                }
            isLoadingPage = false
        }
    }

    /**
     * Reinicia el estado y recarga desde la primera página.
     * Útil para el botón "Reintentar" en estados de error.
     */
    fun retry() {
        currentPage = INITIAL_PAGE
        hasNextPage = true
        isLoadingPage = false
        _uiState.value = CharactersUiState.Loading
        loadNextPage()
    }

    /**
     * Limpia el mensaje de error de paginación del estado de éxito de la UI.
     * Debe llamarse después de que el mensaje de error (Snackbar) se haya mostrado.
     */
    fun clearPagingError() {
        val currentState = _uiState.value
        if (currentState is CharactersUiState.Success) {
            _uiState.value = currentState.copy(pagingError = null)
        }
    }

    /**
     * Actualiza el estado a cargando según si ya hay datos previos o no.
     * Si ya hay datos, muestra el indicador discreto al final del grid.
     * Si es la primera carga, muestra el indicador centrado a pantalla completa.
     */
    private fun updateLoadingState() {
        val currentState = _uiState.value
        if (currentState is CharactersUiState.Success) {
            _uiState.value = currentState.copy(isLoadingNextPage = true)
        }
        // Si el estado es Loading (primera carga), no se modifica — ya está en Loading
    }

    /**
     * Procesa una respuesta exitosa de la API, acumulando los personajes
     * nuevos a la lista existente y actualizando la señal de paginación.
     */
    private fun handleSuccess(paginatedResult: PaginatedResult<Character>) {
        val currentState = _uiState.value
        val existingCharacters = if (currentState is CharactersUiState.Success) {
            currentState.characters
        } else {
            emptyList()
        }

        hasNextPage = paginatedResult.hasNextPage
        currentPage++

        _uiState.value = CharactersUiState.Success(
            characters = existingCharacters + paginatedResult.items,
            favoriteIds = favoriteIds,
            isLoadingNextPage = false,
            hasNextPage = paginatedResult.hasNextPage
        )
    }

    /**
     * Procesa un error diferenciando entre falta de conexión a internet
     * y errores genéricos del servidor. Si es la primera carga y existen
     * datos guardados localmente, los carga como fallback offline.
     */
    private fun handleError(error: Throwable) {
        val currentState = _uiState.value
        // Si ya hay datos cargados, restaurar el estado previo sin el indicador de carga
        if (currentState is CharactersUiState.Success) {
            val message = if (error is UnknownHostException) {
                ERROR_MESSAGE_NO_INTERNET
            } else {
                ERROR_MESSAGE_GENERIC
            }
            _uiState.value = currentState.copy(
                isLoadingNextPage = false,
                pagingError = message
            )
            return
        }

        // Primera carga fallida — Intentamos recuperar datos locales de favoritos
        viewModelScope.launch {
            try {
                val localFavorites = observeFavoriteCharactersUseCase().first()
                if (localFavorites.isNotEmpty()) {
                    hasNextPage = false
                    _uiState.value = CharactersUiState.Success(
                        characters = localFavorites,
                        favoriteIds = localFavorites.map { it.id }.toSet(),
                        isLoadingNextPage = false,
                        hasNextPage = false
                    )
                } else {
                    // Si no hay favoritos locales, mostrar pantalla de error completa
                    showErrorState(error)
                }
            } catch (fallbackError: Exception) {
                showErrorState(error)
            }
        }
    }

    /**
     * Establece el estado de error de la pantalla según el tipo de excepción.
     */
    private fun showErrorState(error: Throwable) {
        val message = if (error is UnknownHostException) {
            ERROR_MESSAGE_NO_INTERNET
        } else {
            ERROR_MESSAGE_GENERIC
        }
        _uiState.value = CharactersUiState.Error(message = message)
    }

    companion object {
        private const val INITIAL_PAGE = 1

        /** Mensaje de error cuando no hay conexión a internet. */
        const val ERROR_MESSAGE_NO_INTERNET =
            "Sin conexión a internet, revisa tu conexión y vuelve a intentarlo"

        /** Mensaje de error genérico para fallos del servidor. */
        const val ERROR_MESSAGE_GENERIC =
            "Error al cargar, inténtelo de nuevo más tarde"
    }
}
