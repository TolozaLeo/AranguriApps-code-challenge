package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow

/**
 * Función composable que rastrea la dirección del scroll de un [LazyGridState]
 * para determinar reactivamente si una barra de filtros o búsqueda debe ser visible.
 *
 * Oculta la barra al hacer scroll hacia abajo y la muestra al hacer scroll hacia arriba,
 * o cuando se encuentra al inicio de la lista. Incorpora un umbral (threshold)
 * para reducir la sensibilidad.
 *
 * @param gridState Estado de la grilla que se desea rastrear.
 * @return Un estado reactivo [Boolean] que indica si la barra debe ser visible.
 */
@Composable
fun rememberScrollDirectionVisibility(gridState: LazyGridState): Boolean {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(gridState) {
        var previousIndex = 0
        var previousScrollOffset = 0
        var accumulatedScroll = 0
        val threshold = 200 // Umbral de sensibilidad en píxeles

        snapshotFlow {
            Triple(
                gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset,
                gridState.isScrollInProgress,
                gridState.canScrollForward
            )
        }.collect { (scrollPosition, isScrolling, canScrollForward) ->
            val (currentIndex, currentOffset) = scrollPosition
            
            if (currentIndex == 0 && currentOffset == 0) {
                isVisible = true
                accumulatedScroll = 0
            } else if (isScrolling && canScrollForward) {
                val delta = if (currentIndex == previousIndex) {
                    currentOffset - previousScrollOffset
                } else {
                    // Si cambia de índice, sumamos el umbral para forzar el cambio si es necesario
                    if (currentIndex > previousIndex) threshold else -threshold
                }

                // Resetear el acumulado si cambiamos de dirección de scroll
                if ((delta > 0 && accumulatedScroll < 0) || (delta < 0 && accumulatedScroll > 0)) {
                    accumulatedScroll = 0
                }
                
                accumulatedScroll += delta

                if (accumulatedScroll >= threshold) {
                    isVisible = false // Deslizando hacia abajo
                    accumulatedScroll = 0
                } else if (accumulatedScroll <= -threshold) {
                    isVisible = true // Deslizando hacia arriba
                    accumulatedScroll = 0
                }
            } else if (!isScrolling) {
                // Reiniciamos el acumulador al detener el scroll
                accumulatedScroll = 0
            }
            
            previousIndex = currentIndex
            previousScrollOffset = currentOffset
        }
    }

    return isVisible
}
