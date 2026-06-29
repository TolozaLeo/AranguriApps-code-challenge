package dev.leotoloza.aranguriappscodechallenge.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ObserveFavoriteCharactersUseCase
import dev.leotoloza.aranguriappscodechallenge.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
     * Inicia la observación reactiva del flujo de personajes favoritos.
     */
    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteCharactersUseCase().collect { characters ->
                if (characters.isEmpty()) {
                    _uiState.value = FavoritesUiState.Empty
                } else {
                    _uiState.value = FavoritesUiState.Success(characters)
                }
            }
        }
    }
}
