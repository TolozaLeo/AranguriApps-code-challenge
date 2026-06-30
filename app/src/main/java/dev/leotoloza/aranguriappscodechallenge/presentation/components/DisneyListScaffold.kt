package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterCategory
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.CategoryColor

/**
 * Un Scaffold estructurado común para pantallas de listados.
 * Maneja la barra superior, snackbars, y opcionalmente integra una barra de búsqueda
 * y filtros de categorías dinámicos que se auto-ocultan al hacer scroll.
 *
 * @param titleText Título de la pantalla.
 * @param gridState Estado de scroll de la grilla principal.
 * @param snackbarHostState Estado de los snackbars.
 * @param modifier Modificador para aplicar al Scaffold.
 * @param showFilters Indica si se deben renderizar e integrar la barra de búsqueda y filtros.
 * @param searchQuery Consulta de búsqueda activa (requerido si [showFilters] es true).
 * @param onQueryChanged Callback de cambio en búsqueda (requerido si [showFilters] es true).
 * @param onSearchTriggered Callback de confirmación de búsqueda (requerido si [showFilters] es true).
 * @param onClearClicked Callback para limpiar la búsqueda (requerido si [showFilters] es true).
 * @param selectedCategory Categoría seleccionada para filtros (requerido si [showFilters] es true).
 * @param onCategorySelected Callback de selección de categoría (requerido si [showFilters] es true).
 * @param allCategoryActiveColor Color personalizado para la categoría activa.
 * @param content Slot de contenido principal que recibe los paddings del Scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisneyListScaffold(
    titleText: String,
    gridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    showFilters: Boolean = false,
    searchQuery: String = "",
    onQueryChanged: (String) -> Unit = {},
    onSearchTriggered: (String) -> Unit = {},
    onClearClicked: () -> Unit = {},
    selectedCategory: CharacterCategory? = null,
    onCategorySelected: (CharacterCategory?) -> Unit = {},
    allCategoryActiveColor: CategoryColor? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val isFilterBarVisible = if (showFilters) {
        rememberScrollDirectionVisibility(gridState = gridState)
    } else {
        false
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            DisneyTopAppBar(
                titleText = titleText,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                DisneySnackbar(snackbarData = data)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            if (showFilters) {
                AnimatedVisibility(
                    visible = isFilterBarVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        DisneySearchBar(
                            query = searchQuery,
                            onQueryChanged = onQueryChanged,
                            onSearchTriggered = onSearchTriggered,
                            onClearClicked = onClearClicked
                        )
                        if (allCategoryActiveColor != null) {
                            CategoryFilterBar(
                                selectedCategory = selectedCategory,
                                onCategorySelected = onCategorySelected,
                                allCategoryActiveColor = allCategoryActiveColor
                            )
                        } else {
                            CategoryFilterBar(
                                selectedCategory = selectedCategory,
                                onCategorySelected = onCategorySelected
                            )
                        }
                    }
                }
            }
            content(
                PaddingValues(
                    bottom = innerPadding.calculateBottomPadding()
                )
            )
        }
    }
}
