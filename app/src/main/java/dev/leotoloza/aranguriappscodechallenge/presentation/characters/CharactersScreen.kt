package dev.leotoloza.aranguriappscodechallenge.presentation.characters

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.DisneyCelestialBlue

/**
 * Pantalla que muestra el listado adaptativo de personajes de Disney con scroll infinito.
 *
 * Observa el estado del [CharactersViewModel] para renderizar los estados de carga,
 * error y contenido.
 *
 * @param modifier Modificador para aplicar a la pantalla.
 * @param gridState Estado de scroll de la grilla de personajes.
 * @param onCharacterClick Callback que se ejecuta al seleccionar un personaje, recibiendo el [Character] completo.
 * @param viewModel ViewModel inyectado por Hilt que gestiona el estado de la pantalla.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun CharactersScreen(
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    onCharacterClick: (Character) -> Unit = {},
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val state = uiState

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

    val activeQuery = (state as? CharactersUiState.Success)?.searchQuery ?: ""
    val (localQuery, setLocalQuery) = remember(activeQuery) {
        mutableStateOf(activeQuery)
    }

    var hasLoadedOnce by remember { mutableStateOf(false) }
    if (state is CharactersUiState.Success) {
        hasLoadedOnce = true
    }

    DisneyListScaffold(
        titleText = stringResource(R.string.characters_title),
        gridState = gridState,
        snackbarHostState = snackbarHostState,
        showFilters = hasLoadedOnce,
        searchQuery = localQuery,
        showSearchButton = localQuery.trim() != activeQuery.trim(),
        onQueryChanged = setLocalQuery,
        onSearchTriggered = viewModel::searchCharacters,
        onClearClicked = {
            setLocalQuery("")
            viewModel.clearSearch()
        },
        selectedCategory = (state as? CharactersUiState.Success)?.selectedCategory,
        onCategorySelected = viewModel::selectCategory,
        modifier = modifier
    ) { innerPadding ->
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
                SuccessContent(
                    state = state,
                    gridState = gridState,
                    onCharacterClick = onCharacterClick,
                    onFavoriteClick = viewModel::toggleFavorite,
                    onLoadMore = viewModel::loadNextPage,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding
                )
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
 * @param modifier Modificador para aplicar a la caja contenedora.
 */
@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray.copy(alpha = TEXT_COLOR_ALPHA)
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.stackSm))
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
 * @param gridState Estado de scroll de la grilla principal.
 * @param onCharacterClick Callback al seleccionar un personaje.
 * @param onFavoriteClick Callback al presionar favoritos sobre un personaje.
 * @param onLoadMore Callback para solicitar la carga de la siguiente página.
 * @param modifier Modificador para aplicar al contenedor.
 * @param contentPadding Padding proporcionado por el Scaffold.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
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

    if (state.characters.isEmpty()) {
        val searchQuery = state.searchQuery
        if (searchQuery.isNotEmpty()) {
            EmptySearchContent(
                title = stringResource(R.string.empty_search_characters_title, searchQuery),
                subtitle = stringResource(R.string.empty_search_characters_subtitle),
                modifier = modifier
            )
        } else if (state.selectedCategory != null) {
            EmptyCategoryContent(
                title = stringResource(
                    R.string.empty_characters_category_title,
                    stringResource(state.selectedCategory.labelResId)
                ),
                subtitle = stringResource(R.string.empty_characters_category_subtitle),
                modifier = modifier
            )
        }
    } else {
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
                items = state.characters, key = { character -> character.id }) { character ->
                val isFavorite = state.favoriteIds.contains(character.id)
                CharacterCard(
                    character = character,
                    initialIsFavorite = isFavorite,
                    borderColor = DisneyCelestialBlue.copy(alpha = CARD_BORDER_ALPHA),
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
                            color = AppTheme.colors.primary, modifier = Modifier.size(PAGINATION_LOADER_SIZE)
                        )
                    }
                }
            }
        }
    }
}

/** Umbral de ítems para scroll infinito. */
private const val LOAD_MORE_THRESHOLD = 10

/** Ancho de columna adaptable de la grilla de personajes. */
private val GRID_COLUMN_WIDTH = 340.dp

/** Alpha del borde para la tarjeta de personaje. */
private const val CARD_BORDER_ALPHA = 0.25f

/** Tiempo de entrada de la animación de ítems en milisegundos. */
private const val FADE_IN_DURATION = 250

/** Tiempo de salida de la animación de ítems en milisegundos. */
private const val FADE_OUT_DURATION = 300

/** Tamaño del indicador de carga de la paginación. */
private val PAGINATION_LOADER_SIZE = 24.dp

/** Opacidad semitransparente para textos secundarios o auxiliares. */
private const val TEXT_COLOR_ALPHA = 0.6f