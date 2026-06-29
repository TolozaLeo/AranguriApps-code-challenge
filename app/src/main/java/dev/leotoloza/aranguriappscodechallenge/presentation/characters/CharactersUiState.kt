package dev.leotoloza.aranguriappscodechallenge.presentation.characters

import dev.leotoloza.aranguriappscodechallenge.domain.model.Character

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
     * @property isLoadingNextPage Indica si se está cargando la siguiente página (indicador discreto al final del grid).
     * @property hasNextPage Indica si existen más páginas disponibles para cargar.
     */
    data class Success(
        val characters: List<Character>,
        val isLoadingNextPage: Boolean,
        val hasNextPage: Boolean
    ) : CharactersUiState

    /**
     * Error de red o del servidor.
     *
     * @property message Mensaje descriptivo del error para mostrar al usuario.
     */
    data class Error(val message: String) : CharactersUiState
}
