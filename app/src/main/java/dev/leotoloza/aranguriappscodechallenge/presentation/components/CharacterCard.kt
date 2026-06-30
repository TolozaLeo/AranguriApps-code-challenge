package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import dev.leotoloza.aranguriappscodechallenge.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.activeCategories
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.FavoriteCoral

/**
 * Tarjeta reutilizable que representa de forma visual a un personaje de Disney.
 *
 * Incluye un botón de favoritos interactivo en el extremo derecho con una animación elástica.
 * Utiliza el estilo [AppTheme.styles.characterCardStyle] para mantener la consistencia visual.
 *
 * @param character Modelo de dominio del personaje a representar.
 * @param modifier Modificador para aplicar a la tarjeta.
 * @param initialIsFavorite Estado inicial de favoritos del personaje.
 * @param borderColor Color del borde de la tarjeta. Por defecto es transparente.
 * @param borderWidth Ancho/grosor del borde de la tarjeta. Por defecto es 1.dp.
 * @param onFavoriteClick Callback que se ejecuta al presionar el botón de favoritos. Recibe el nuevo estado.
 * @param onClick Callback que se ejecuta al presionar o hacer clic en la tarjeta.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun CharacterCard(
    character: Character,
    modifier: Modifier = Modifier,
    initialIsFavorite: Boolean = false,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 1.dp,
    onFavoriteClick: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    var isFavorite by remember(initialIsFavorite) { mutableStateOf(initialIsFavorite) }

    // Animación de escala elástica (bouncy spring) para el efecto "pop" al interactuar
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1.0f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        ), label = "FavoriteButtonScale"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (borderColor != Color.Transparent) BorderStroke(borderWidth, borderColor) else null,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen del personaje ocupando el total del alto de la card y siendo cuadrada
                DisneyAsyncImage(
                    imageUrl = character.imageUrl,
                    contentDescription = stringResource(R.string.character_image_desc, character.name),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                )
                Spacer(modifier = Modifier.width(AppTheme.spacing.stackMd))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = AppTheme.spacing.stackSm)
                        .padding(end = 40.dp) // Evita que nombres largos colisionen con el corazón
                ) {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.stackSm))
                    // Tags de categoría según las categorías activas del personaje
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.stackSm),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.stackSm),
                        maxLines = 2
                    ) {
                        character.activeCategories().forEach { category ->
                            CategoryTag(
                                text = stringResource(category.labelResId),
                                colors = category.categoryColor
                            )
                        }
                    }
                }
            }

            // Botón de favoritos flotante en la esquina superior derecha de la tarjeta
            IconButton(
                onClick = {
                    isFavorite = !isFavorite
                    onFavoriteClick?.invoke(isFavorite)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(if (isFavorite) R.string.remove_favorite_desc else R.string.add_favorite_desc),
                    tint = if (isFavorite) FavoriteCoral else AppTheme.colors.onSurface.copy(
                        alpha = 0.6f
                    ),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
