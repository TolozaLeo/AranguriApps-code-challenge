package dev.leotoloza.aranguriappscodechallenge.presentation.favorites

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.leotoloza.aranguriappscodechallenge.R
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.presentation.components.CharacterCard
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneyListScaffold
import dev.leotoloza.aranguriappscodechallenge.presentation.components.EmptyCategoryContent
import dev.leotoloza.aranguriappscodechallenge.presentation.components.EmptySearchContent
import dev.leotoloza.aranguriappscodechallenge.presentation.components.labelResId
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.CategoryColor
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.FavoriteCoral
import kotlinx.coroutines.launch

/**
 * Pantalla que muestra la lista adaptativa de personajes favoritos de Disney de forma reactiva.
 *
 * @param modifier Modificador para aplicar a la pantalla.
 * @param onCharacterClick Callback que se ejecuta al seleccionar un personaje, recibiendo el [Character] completo.
 * @param viewModel ViewModel de Hilt que gestiona la carga y actualización de los favoritos.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onCharacterClick: (Character) -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    val state = uiState
    val activeQuery = (state as? FavoritesUiState.Success)?.searchQuery ?: ""
    val (localQuery, setLocalQuery) = remember(activeQuery) {
        mutableStateOf(activeQuery)
    }

    DisneyListScaffold(
        titleText = stringResource(R.string.favorites_title),
        gridState = gridState,
        snackbarHostState = snackbarHostState,
        showFilters = state is FavoritesUiState.Success,
        searchQuery = localQuery,
        showSearchButton = localQuery.trim() != activeQuery.trim(),
        onQueryChanged = setLocalQuery,
        onSearchTriggered = viewModel::searchCharacters,
        onClearClicked = {
            setLocalQuery("")
            viewModel.clearSearch()
        },
        selectedCategory = (state as? FavoritesUiState.Success)?.selectedCategory,
        onCategorySelected = viewModel::selectCategory,
        allCategoryActiveColor = CategoryColor(
            background = FavoriteCoral,
            text = Color.White
        ),
        modifier = modifier
    ) { innerPadding ->
        when (state) {
            is FavoritesUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(innerPadding))
            }

            is FavoritesUiState.Empty -> {
                EmptyContent(modifier = Modifier.padding(innerPadding))
            }

            is FavoritesUiState.Success -> {
                if (state.characters.isEmpty()) {
                    if (state.searchQuery.isNotEmpty()) {
                        EmptySearchContent(
                            title = stringResource(R.string.empty_search_favorites_title, state.searchQuery),
                            subtitle = stringResource(R.string.empty_search_favorites_subtitle),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        EmptyCategoryContent(
                            title = stringResource(
                                R.string.empty_favorites_category_title,
                                state.selectedCategory?.labelResId?.let { stringResource(it) }.orEmpty()
                            ),
                            subtitle = stringResource(R.string.empty_favorites_category_subtitle),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    val removedMessageTemplate = stringResource(R.string.character_removed_from_favorites)
                    val undoActionLabel = stringResource(R.string.undo_action)

                    SuccessContent(
                        characters = state.characters,
                        gridState = gridState,
                        onCharacterClick = onCharacterClick,
                        onFavoriteClick = { character ->
                            viewModel.toggleFavorite(character)
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                val result = snackbarHostState.showSnackbar(
                                    message = removedMessageTemplate.format(character.name),
                                    actionLabel = undoActionLabel,
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.toggleFavorite(character)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
}

/**
 * Indicador de carga centrado para la pantalla de favoritos.
 *
 * @param modifier Modificador para aplicar al contenedor.
 */
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = FavoriteCoral
        )
    }
}

/**
 * Mensaje decorativo y centrado mostrado cuando no hay favoritos guardados.
 *
 * @param modifier Modificador para aplicar al contenedor.
 */
@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(AppTheme.spacing.stackLg)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = FavoriteCoral.copy(alpha = ICON_ALPHA),
                modifier = Modifier.size(EMPTY_ICON_SIZE)
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.stackMd))
            Text(
                text = stringResource(R.string.empty_favorites_title),
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface.copy(alpha = TEXT_MEDIUM_ALPHA),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.stackSm))
            Text(
                text = stringResource(R.string.empty_favorites_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurface.copy(alpha = TEXT_LIGHT_ALPHA),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Contenido principal con la grilla de personajes favoritos.
 *
 * @param characters Lista de personajes favoritos a mostrar.
 * @param gridState Estado de scroll de la grilla principal.
 * @param onCharacterClick Callback al presionar una tarjeta.
 * @param onFavoriteClick Callback para alternar el estado de favorito.
 * @param modifier Modificador para aplicar al contenedor.
 * @param contentPadding Padding de contenido para la grilla.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
private fun SuccessContent(
    characters: List<Character>,
    gridState: LazyGridState,
    onCharacterClick: (Character) -> Unit,
    onFavoriteClick: (Character) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(GRID_COLUMN_WIDTH),
        state = gridState,
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = AppTheme.spacing.marginPage),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gutter),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.stackMd)
    ) {
        items(
            items = characters, key = { character -> character.id }) { character ->
            CharacterCard(
                character = character,
                initialIsFavorite = true,
                borderColor = FavoriteCoral.copy(alpha = CARD_BORDER_ALPHA),
                onFavoriteClick = { onFavoriteClick(character) },
                onClick = { onCharacterClick(character) },
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = FADE_IN_DURATION),
                    fadeOutSpec = tween(durationMillis = FADE_OUT_DURATION),
                    placementSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            )
        }
    }
}

/** Ancho de columna adaptable de la grilla de personajes favoritos. */
private val GRID_COLUMN_WIDTH = 340.dp

/** Alpha del borde para la tarjeta de personaje favorito. */
private const val CARD_BORDER_ALPHA = 0.35f

/** Tiempo de entrada de la animación de ítems en milisegundos. */
private const val FADE_IN_DURATION = 250

/** Tiempo de salida de la animación de ítems en milisegundos. */
private const val FADE_OUT_DURATION = 300

/** Tamaño del icono decorativo de la pantalla vacía. */
private val EMPTY_ICON_SIZE = 64.dp

/** Alpha para el icono decorativo. */
private const val ICON_ALPHA = 0.4f

/** Opacidad semitransparente media para títulos secundarios. */
private const val TEXT_MEDIUM_ALPHA = 0.6f

/** Opacidad semitransparente ligera para textos informativos del subtítulo. */
private const val TEXT_LIGHT_ALPHA = 0.4f
