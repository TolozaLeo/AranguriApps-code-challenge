package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme

/**
 * Tarjeta reutilizable que representa de forma visual a un personaje de Disney.
 *
 * Incluye un botón de favoritos interactivo en el extremo derecho con una animación elástica.
 * Utiliza el estilo [AppTheme.styles.characterCardStyle] para mantener la consistencia visual.
 *
 * @param name Nombre del personaje que se mostrará en la tarjeta.
 * @param modifier Modificador para aplicar a la tarjeta.
 * @param initialIsFavorite Estado inicial de favoritos del personaje.
 * @param onFavoriteClick Callback que se ejecuta al presionar el botón de favoritos. Recibe el nuevo estado.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun CharacterCard(
    name: String,
    modifier: Modifier = Modifier,
    initialIsFavorite: Boolean = false,
    onFavoriteClick: ((Boolean) -> Unit)? = null
) {
    var isFavorite by remember { mutableStateOf(initialIsFavorite) }
    val interactionSource = remember { MutableInteractionSource() }

    // Animación de escala elástica (bouncy spring) para el efecto "pop" al interactuar
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "FavoriteButtonScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .styleable(null, AppTheme.styles.characterCardStyle)
            .padding(AppTheme.spacing.stackMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(AppTheme.colors.primaryContainer)
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.stackMd))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.stackSm))
            // TODO: Reemplazar por etiquetas de categoría reales de Disney
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelSmall
            )
        }
        
        // Botón de favoritos responsivo e interactivo usando símbolos Unicode de corazón
        IconButton(
            onClick = {
                isFavorite = !isFavorite
                onFavoriteClick?.invoke(isFavorite)
            },
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        ) {
            Text(
                text = if (isFavorite) "\u2665" else "\u2661",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isFavorite) AppTheme.colors.primary else AppTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
