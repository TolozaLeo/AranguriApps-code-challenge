package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme

/**
 * Componente de imagen reutilizable que maneja la carga asíncrona de imágenes de forma elegante.
 *
 * Muestra un [CircularProgressIndicator] centrado mientras la imagen se está descargando y un
 * [Icon] genérico si la imagen no se encuentra o si ocurre un error. Utiliza [SubcomposeAsyncImage]
 * de Coil para manejar estos estados de forma declarativa.
 *
 * @param imageUrl La URL de la imagen a cargar. Si es nulo o vacío, mostrará el estado de error.
 * @param contentDescription Descripción textual de la imagen para accesibilidad.
 * @param modifier Modificador para aplicar al contenedor de la imagen.
 * @param contentScale Define cómo la imagen debe escalarse dentro de los límites del contenedor.
 */
@Composable
fun DisneyAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    SubcomposeAsyncImage(
        model = imageUrl?.ifEmpty { null },
        contentDescription = contentDescription,
        modifier = modifier.background(AppTheme.colors.primaryContainer),
        contentScale = contentScale,
        loading = {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = AppTheme.colors.primary,
                    strokeWidth = 2.dp
                )
            }
        },
        error = {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Imagen no encontrada",
                    tint = AppTheme.colors.onPrimaryContainer,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    )
}
