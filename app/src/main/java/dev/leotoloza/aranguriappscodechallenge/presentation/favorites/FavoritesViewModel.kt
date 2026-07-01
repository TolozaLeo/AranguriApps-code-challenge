package dev.leotoloza.aranguriappscodechallenge.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterCategory
import dev.leotoloza.aranguriappscodechallenge.domain.model.activeCategories
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ObserveFavoriteCharactersUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que gestiona y expone el estado de la pantalla de favoritos.
 *
 * Observa continuamente los personajes favoritos y publica actualizaciones
 * en un flujo reactivo compatible con Compose.
 *
 * @property observeFavoriteCharactersUseCase Caso de uso para observar la lista de favoritos.
 * @property toggleFavoriteUseCase Caso de uso para alternar el estado de favorito de un personaje.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavoriteCharactersUseCase: ObserveFavoriteCharactersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)

    /**
     * Flujo reactivo del estado de UI observable desde la vista.
     */
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val selectedCategoryFlow = MutableStateFlow<CharacterCategory?>(null)
    private val searchQueryFlow = MutableStateFlow("")

    init {
        observeFavorites()
    }

    /**
     * Alterna la pertenencia del personaje seleccionado a favoritos.
     *
     * @param character Personaje a agregar o quitar de la lista.
     */
    fun toggleFavorite(character: Character) {
        viewModelScope.launch {
            toggleFavoriteUseCase(character)
        }
    }

    /**
     * Actualiza la categoría seleccionada para filtrar visualmente los favoritos en la UI.
     *
     * @param category Categoría de personajes por la que se desea filtrar, o `null` para mostrar todos.
     */
    fun selectCategory(category: CharacterCategory?) {
        selectedCategoryFlow.value = category
    }

    /**
     * Establece la consulta de búsqueda para filtrar localmente la lista de favoritos por nombre.
     *
     * @param query Término de búsqueda.
     */
    fun searchCharacters(query: String) {
        searchQueryFlow.value = query.trim()
    }

    /**
     * Limpia la consulta de búsqueda activa para volver a mostrar todos los favoritos.
     */
    fun clearSearch() {
        searchQueryFlow.value = ""
    }

    /**
     * Inicia la observación reactiva del flujo de personajes favoritos combinando
     * los filtros de categoría y de búsqueda por nombre.
     */
    private fun observeFavorites() {
        viewModelScope.launch {
            combine(
                observeFavoriteCharactersUseCase(),
                selectedCategoryFlow,
                searchQueryFlow
            ) { characters, category, query ->
                if (characters.isEmpty()) {
                    FavoritesUiState.Empty
                } else {
                    // Filtrar la lista local en memoria (100% libre de inyección de código/SQL)
                    val filtered = characters
                        .filter { category == null || it.activeCategories().contains(category) }
                        .filter { query.isEmpty() || it.name.contains(query, ignoreCase = true) }

                    FavoritesUiState.Success(
                        characters = filtered,
                        selectedCategory = category,
                        searchQuery = query
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
