package dev.leotoloza.aranguriappscodechallenge.presentation.characters

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterCategory

/**
 * Modela los estados posibles de la pantalla de personajes.
 *
 * Los estados siguen la especificación de diseño (sección "SCREEN STATES & UX CODES"):
 * - [Loading]: Carga inicial con indicador circular centrado.
 * - [Success]: Lista de personajes cargada exitosamente con soporte de paginación.
 * - [Error]: Error de red o del servidor con opción de reintentar.
 */
sealed interface CharactersUiState {

    /** Carga inicial — CircularProgressIndicator centrado en pantalla. */
    data object Loading : CharactersUiState

    /**
     * Lista cargada exitosamente con soporte de paginación.
     *
     * @property characters Lista acumulada de personajes cargados hasta el momento.
     * @property favoriteIds Conjunto de identificadores de personajes marcados como favoritos.
     * @property isLoadingNextPage Indica si se está cargando la siguiente página (indicador discreto al final del grid).
     * @property hasNextPage Indica si existen más páginas disponibles para cargar.
     * @property pagingError Mensaje de error para mostrar al cargar una página adicional.
     * @property selectedCategory Categoría actualmente seleccionada para el filtro visual.
     */
    data class Success(
        val characters: List<Character>,
        val favoriteIds: Set<Int> = emptySet(),
        val isLoadingNextPage: Boolean,
        val hasNextPage: Boolean,
        val pagingError: String? = null,
        val selectedCategory: CharacterCategory? = null
    ) : CharactersUiState

    /**
     * Error de red o del servidor.
     *
     * @property message Mensaje descriptivo del error para mostrar al usuario.
     */
    data class Error(val message: String) : CharactersUiState
}
