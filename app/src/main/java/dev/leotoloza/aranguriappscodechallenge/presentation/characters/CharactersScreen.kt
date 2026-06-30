package dev.leotoloza.aranguriappscodechallenge.presentation.characters

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.activeCategories
import dev.leotoloza.aranguriappscodechallenge.presentation.components.CategoryFilterBar
import dev.leotoloza.aranguriappscodechallenge.presentation.components.CharacterCard
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneySnackbar
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneyTopAppBar
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneySearchBar
import androidx.compose.ui.res.stringResource
import dev.leotoloza.aranguriappscodechallenge.R
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme

/**
 * Pantalla que muestra el listado adaptativo de personajes de Disney con scroll infinito.
 *
 * Observa el estado del [CharactersViewModel] para renderizar los estados de carga,
 * error y contenido. Implementa paginación automática al detectar proximidad al
 * final de la lista.
 *
 * @param onCharacterClick Callback que se ejecuta al seleccionar un personaje, recibiendo el [Character] completo.
 * @param modifier Modificador para aplicar a la pantalla.
 * @param viewModel ViewModel inyectado por Hilt que gestiona el estado de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationStyleApi::class)
@Composable
fun CharactersScreen(
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    onCharacterClick: (Character) -> Unit = {},
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    val state = uiState
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

    val retryLabel = stringResource(R.string.retry_button)
    if (state is CharactersUiState.Success) {
        val pagingError = state.pagingError
        LaunchedEffect(pagingError) {
            if (pagingError != null) {
                val result = snackbarHostState.showSnackbar(
                    message = pagingError,
                    actionLabel = retryLabel,
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.loadNextPage()
                }
                viewModel.clearPagingError()
            }
        }
    }

    Scaffold(modifier = modifier, topBar = {
        DisneyTopAppBar(
            titleText = stringResource(R.string.characters_title), scrollBehavior = scrollBehavior
        )
    }, snackbarHost = {
        SnackbarHost(hostState = snackbarHostState) { data ->
            DisneySnackbar(snackbarData = data)
        }
    }) { innerPadding ->
        when (state) {
            is CharactersUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(innerPadding))
            }

            is CharactersUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = viewModel::retry,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is CharactersUiState.Success -> {
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
                                mutableStateOf(state.searchQuery ?: "")
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
                    SuccessContent(
                        state = state,
                        gridState = gridState,
                        onCharacterClick = onCharacterClick,
                        onFavoriteClick = viewModel::toggleFavorite,
                        onLoadMore = viewModel::loadNextPage,
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

/**
 * Estado de carga inicial con indicador circular centrado en pantalla.
 * Color: Disney Celestial Blue (#238EC1) según especificación de diseño.
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
 * Estado de error con mensaje descriptivo y botón de reintentar.
 * Los mensajes se definen en [CharactersViewModel.Companion] para evitar magic strings.
 *
 * @param message Mensaje de error a mostrar al usuario.
 * @param onRetry Callback para reintentar la carga.
 */
@Composable
private fun ErrorContent(
    message: String, onRetry: () -> Unit, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.retry_button))
            }
        }
    }
}

/**
 * Contenido principal con la grilla adaptativa de personajes y scroll infinito.
 *
 * Detecta proximidad al final de la lista usando [derivedStateOf] sobre el estado
 * del grid y dispara la carga de la siguiente página automáticamente. Muestra un
 * indicador de paginación discreto al final del grid durante la carga.
 *
 * @param state Estado exitoso con la lista de personajes y metadatos de paginación.
 * @param onCharacterClick Callback al seleccionar un personaje.
 * @param onFavoriteClick Callback al presionar favoritos sobre un personaje.
 * @param onLoadMore Callback para solicitar la carga de la siguiente página.
 * @param innerPadding Padding proporcionado por el Scaffold.
 */
@Composable
private fun SuccessContent(
    state: CharactersUiState.Success,
    gridState: LazyGridState,
    onCharacterClick: (Character) -> Unit,
    onFavoriteClick: (Character) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    // Detección de scroll infinito: dispara la carga cuando el usuario se acerca al final
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem =
                gridState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisibleItem.index >= totalItems - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    val filteredCharacters = remember(state.characters, state.selectedCategory) {
        val category = state.selectedCategory
        if (category == null) {
            state.characters
        } else {
            state.characters.filter { character ->
                character.activeCategories().contains(category)
            }
        }
    }

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
            items = filteredCharacters, key = { character -> character.id }) { character ->
            val isFavorite = state.favoriteIds.contains(character.id)
            CharacterCard(
                character = character,
                initialIsFavorite = isFavorite,
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

        // Indicador de paginación discreto al final del grid
        if (state.isLoadingNextPage) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppTheme.spacing.stackMd),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AppTheme.colors.primary, modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Umbral de ítems restantes antes del final de la lista para disparar la carga
 * de la siguiente página. Un valor de 10 proporciona una experiencia fluida de
 * scroll infinito sin que el usuario perciba la carga.
 */
private const val LOAD_MORE_THRESHOLD = 10