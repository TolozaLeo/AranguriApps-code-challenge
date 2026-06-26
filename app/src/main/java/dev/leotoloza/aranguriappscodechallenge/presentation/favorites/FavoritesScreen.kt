package dev.leotoloza.aranguriappscodechallenge.presentation.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.leotoloza.aranguriappscodechallenge.presentation.components.CharacterCard
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme

/**
 * Pantalla que muestra la lista adaptativa de personajes favoritos de Disney.
 *
 * @param modifier Modificador para aplicar a la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationStyleApi::class)
@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(340.dp),
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize().padding(horizontal = AppTheme.spacing.marginPage),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gutter),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.stackMd)
        ) {
            items(3) { index ->
                CharacterCard(name = "Favorite $index", initialIsFavorite = true)
            }
        }
    }
}