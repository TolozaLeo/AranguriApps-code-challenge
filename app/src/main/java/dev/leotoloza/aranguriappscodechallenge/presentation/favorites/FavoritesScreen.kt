package dev.leotoloza.aranguriappscodechallenge.presentation.favorites

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.presentation.components.CharacterCard
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneySnackbar
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneyTopAppBar
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

    Scaffold(
        modifier = modifier,
        topBar = {
            DisneyTopAppBar(
                titleText = "Favoritos",
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                DisneySnackbar(snackbarData = data)
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is FavoritesUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(innerPadding))
            }

            is FavoritesUiState.Empty -> {
                EmptyContent(modifier = Modifier.padding(innerPadding))
            }

            is FavoritesUiState.Success -> {
                SuccessContent(
                    characters = state.characters,
                    onCharacterClick = onCharacterClick,
                    onFavoriteClick = { character ->
                        viewModel.toggleFavorite(character)
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            val result = snackbarHostState.showSnackbar(
                                message = "${character.name} eliminado de favoritos",
                                actionLabel = "Deshacer",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.toggleFavorite(character)
                            }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
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
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = AppTheme.colors.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Aún no tienes personajes favoritos",
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explora y marca corazones en la pantalla de Personajes",
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
    onCharacterClick: (Character) -> Unit,
    onFavoriteClick: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(340.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = AppTheme.spacing.marginPage),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gutter),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.stackMd)
    ) {
        items(
            items = characters,
            key = { character -> character.id }
        ) { character ->
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
