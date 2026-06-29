package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme

/**
 * Componente de Snackbar personalizado con la paleta de colores y el estilo del tema de la aplicación.
 *
 * Diseñado para ser reutilizado en múltiples pantallas (como favoritos y personajes)
 * garantizando la consistencia visual y de diseño premium.
 *
 * @param snackbarData Los datos provistos por el Scaffold u host de Snackbar para ser mostrados.
 * @param modifier Modificador para aplicar al componente.
 */
@Composable
fun DisneySnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        snackbarData = snackbarData,
        containerColor = AppTheme.colors.background,
        contentColor = AppTheme.colors.onPrimaryContainer,
        actionColor = AppTheme.colors.primary,
        actionContentColor = AppTheme.colors.primary,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    )
}
