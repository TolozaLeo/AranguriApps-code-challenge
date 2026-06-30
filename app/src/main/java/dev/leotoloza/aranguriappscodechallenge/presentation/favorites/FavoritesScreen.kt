package dev.leotoloza.aranguriappscodechallenge.presentation.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.leotoloza.aranguriappscodechallenge.R
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.presentation.components.CategoryFilterBar
import dev.leotoloza.aranguriappscodechallenge.presentation.components.CharacterCard
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneySearchBar
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneySnackbar
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneyTopAppBar
import dev.leotoloza.aranguriappscodechallenge.presentation.components.labelResId
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme
import kotlinx.coroutines.launch

/**
 * Pantalla que muestra la lista adaptativa de personajes favoritos de Disney de forma reactiva.
 *
 * @param onCharacterClick Callback que se ejecuta al seleccionar un personaje, recibiendo el [Character] completo.
 * @param modifier Modificador para aplicar a la pantalla.
 * @param viewModel ViewModel de Hilt que gestiona la carga y actualización de los favoritos.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationStyleApi::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onCharacterClick: (Character) -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()
    var isFilterBarVisible by remember { mutableStateOf(true) }

    LaunchedEffect(gridState) {
        var previousIndex = 0
        var previousScrollOffset = 0
        snapshotFlow {
            Triple(
                gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset,
                gridState.isScrollInProgress,
                gridState.canScrollForward
            )
        }.collect { (scrollPosition, isScrolling, canScrollForward) ->
            val (currentIndex, currentOffset) = scrollPosition
            if (currentIndex == 0 && currentOffset == 0) {
                isFilterBarVisible = true
            } else if (isScrolling && canScrollForward) {
                if (currentIndex > previousIndex || (currentIndex == previousIndex && currentOffset > previousScrollOffset)) {
                    isFilterBarVisible = false // Deslizando hacia abajo
                } else if (currentIndex < previousIndex || (currentOffset < previousScrollOffset)) {
                    isFilterBarVisible = true // Deslizando hacia arriba
                }
            }
            previousIndex = currentIndex
            previousScrollOffset = currentOffset
        }
    }

    Scaffold(modifier = modifier, topBar = {
        DisneyTopAppBar(
            titleText = stringResource(R.string.favorites_title), scrollBehavior = scrollBehavior
        )
    }, snackbarHost = {
        SnackbarHost(hostState = snackbarHostState) { data ->
            DisneySnackbar(snackbarData = data)
        }
    }) { innerPadding ->
        when (val state = uiState) {
            is FavoritesUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(innerPadding))
            }

            is FavoritesUiState.Empty -> {
                EmptyContent(modifier = Modifier.padding(innerPadding))
            }

            is FavoritesUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                AnimatedVisibility(
                    visible = isFilterBarVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        var localQuery by remember(state.searchQuery) {
                            mutableStateOf(state.searchQuery)
                        }

                        DisneySearchBar(
                            query = localQuery,
                            onQueryChanged = { localQuery = it },
                            onSearchTriggered = { query ->
                                viewModel.searchCharacters(query)
                            },
                            onClearClicked = {
                                localQuery = ""
                                viewModel.clearSearch()
                            }
                        )
                        CategoryFilterBar(
                            selectedCategory = state.selectedCategory,
                            onCategorySelected = viewModel::selectCategory
                        )
                    }
                }

                if (state.characters.isEmpty()) {
                    if (state.searchQuery.isNotEmpty()) {
                        EmptySearchContent(
                            query = state.searchQuery,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        EmptyCategoryContent(
                            categoryLabel = state.selectedCategory?.labelResId?.let { stringResource(it) }.orEmpty(),
                            modifier = Modifier.weight(1f)
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
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            bottom = innerPadding.calculateBottomPadding()
                        )
                    )
                }
            }
            }
        }
    }
}

/**
 * Indicador de carga centrado para la pantalla de favoritos.
 */
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = AppTheme.colors.primary
        )
    }
}

/**
 * Mensaje decorativo y centrado mostrado cuando no hay favoritos guardados.
 */
@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = AppTheme.colors.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.empty_favorites_title),
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.empty_favorites_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Contenido principal con la grilla de personajes favoritos.
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
        columns = GridCells.Adaptive(340.dp),
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
                onFavoriteClick = { onFavoriteClick(character) },
                onClick = { onCharacterClick(character) },
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 300),
                    placementSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            )
        }
    }
}

/**
 * Contenido mostrado cuando existen favoritos agregados pero ninguno coincide con la categoría del filtro.
 */
@Composable
private fun EmptyCategoryContent(
    categoryLabel: String, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.empty_favorites_category_title, categoryLabel),
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.empty_favorites_category_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Contenido mostrado cuando la búsqueda de favoritos no arroja resultados.
 */
@Composable
private fun EmptySearchContent(
    query: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.empty_search_favorites_title, query),
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.empty_search_favorites_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}
