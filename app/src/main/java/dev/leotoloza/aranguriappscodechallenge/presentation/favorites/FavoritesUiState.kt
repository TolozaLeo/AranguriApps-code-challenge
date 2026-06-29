package dev.leotoloza.aranguriappscodechallenge.presentation.favorites

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character

/**
 * Modela los estados posibles de la pantalla de personajes favoritos.
 */
sealed interface FavoritesUiState {

    /**
     * Estado de carga inicial.
     */
    data object Loading : FavoritesUiState

    /**
     * Estado que indica que no hay personajes favoritos guardados.
     */
    data object Empty : FavoritesUiState

    /**
     * Estado exitoso que contiene la lista de personajes favoritos.
     *
     * @property characters Lista de [Character] marcados como favoritos.
     */
    data class Success(val characters: List<Character>) : FavoritesUiState
}
