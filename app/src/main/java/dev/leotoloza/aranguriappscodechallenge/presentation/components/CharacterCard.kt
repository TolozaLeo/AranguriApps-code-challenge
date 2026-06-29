package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme

/**
 * Tarjeta reutilizable que representa de forma visual a un personaje de Disney.
 *
 * Incluye un botón de favoritos interactivo en el extremo derecho con una animación elástica.
 * Utiliza el estilo [AppTheme.styles.characterCardStyle] para mantener la consistencia visual.
 *
 * @param character Modelo de dominio del personaje a representar.
 * @param modifier Modificador para aplicar a la tarjeta.
 * @param initialIsFavorite Estado inicial de favoritos del personaje.
 * @param onFavoriteClick Callback que se ejecuta al presionar el botón de favoritos. Recibe el nuevo estado.
 * @param onClick Callback que se ejecuta al presionar o hacer clic en la tarjeta.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun CharacterCard(
    character: Character,
    modifier: Modifier = Modifier,
    initialIsFavorite: Boolean = false,
    onFavoriteClick: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(initialIsFavorite) }
    val interactionSource = remember { MutableInteractionSource() }

    // Animación de escala elástica (bouncy spring) para el efecto "pop" al interactuar
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1.0f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        ), label = "FavoriteButtonScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .styleable(null, AppTheme.styles.characterCardStyle)
            .clickable { onClick() }
            .padding(AppTheme.spacing.stackMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen del personaje usando componente reutilizable
        DisneyAsyncImage(
            imageUrl = character.imageUrl,
            contentDescription = "Imagen de ${character.name}",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.stackMd))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.stackSm))
            // TODO: Reemplazar por etiquetas de categoría reales (FlowRow con tags glow)
            Text(
                text = "Category", style = MaterialTheme.typography.labelSmall
            )
        }

        // Botón de favoritos responsivo e interactivo usando iconos de la librería Material
        IconButton(onClick = {
            isFavorite = !isFavorite
            onFavoriteClick?.invoke(isFavorite)
        }, modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                tint = if (isFavorite) AppTheme.colors.primary else AppTheme.colors.onSurface.copy(
                    alpha = 0.6f
                ),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
