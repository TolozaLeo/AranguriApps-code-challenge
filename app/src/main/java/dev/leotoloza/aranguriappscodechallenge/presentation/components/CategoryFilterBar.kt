package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterCategory
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.CategoryColor

private val OrderedCategories = listOf(
    CharacterCategory.FILM,
    CharacterCategory.VIDEO_GAME,
    CharacterCategory.TV_SHOW,
    CharacterCategory.SHORT_FILM
)

/**
 * Componente interactivo que renderiza una barra de filtrado horizontal por categorías.
 *
 * Muestra chips redondeados para cada categoría disponible y uno para la opción
 * global "Todos" al principio.
 *
 * @param selectedCategory Categoría seleccionada actualmente, o `null` si está seleccionado "Todos".
 * @param onCategorySelected Callback invocado cuando se selecciona una nueva categoría.
 * @param modifier Modificador para aplicar al contenedor.
 */
@Composable
fun CategoryFilterBar(
    selectedCategory: CharacterCategory?,
    onCategorySelected: (CharacterCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val backgroundColor = MaterialTheme.colorScheme.background

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Text(
            text = "Filtrar por:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(
                start = AppTheme.spacing.marginPage + 4.dp,
                top = AppTheme.spacing.stackSm,
                end = AppTheme.spacing.marginPage
            )
        )
        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    // Dibuja primero el contenido del LazyRow
                    drawContent()

                    val fadeWidth = 24.dp.toPx()

                    // Degradado a la izquierda si se puede deslizar hacia atrás
                    if (lazyListState.canScrollBackward) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(backgroundColor, Color.Transparent),
                                startX = 0f,
                                endX = fadeWidth
                            ),
                            topLeft = Offset.Zero,
                            size = androidx.compose.ui.geometry.Size(fadeWidth, size.height)
                        )
                    }

                    // Degradado a la derecha si se puede deslizar hacia adelante
                    if (lazyListState.canScrollForward) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, backgroundColor),
                                startX = size.width - fadeWidth,
                                endX = size.width
                            ),
                            topLeft = Offset(size.width - fadeWidth, 0f),
                            size = androidx.compose.ui.geometry.Size(fadeWidth, size.height)
                        )
                    }
                },
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.stackSm),
            contentPadding = PaddingValues(
                horizontal = AppTheme.spacing.marginPage, vertical = AppTheme.spacing.stackSm
            )
        ) {
            // Chip "Todos" al principio
            item {
                FilterChipItem(
                    text = "Todos",
                    isSelected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    activeColor = CategoryColor(
                        background = MaterialTheme.colorScheme.primary,
                        text = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            // Chips de cada categoría ordenados
            items(
                items = OrderedCategories, key = { category -> category.name }) { category ->
                FilterChipItem(
                    text = category.label,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    activeColor = category.categoryColor
                )
            }
        }
    }
}

/**
 * Componente interno que representa un chip individual interactivo dentro de la barra de filtros.
 *
 * Aplica transiciones animadas de color para una experiencia premium y fluida.
 *
 * @param text Texto a mostrar dentro del chip.
 * @param isSelected Define si el chip está en estado seleccionado.
 * @param onClick Callback que maneja el click sobre el chip.
 * @param activeColor Colores asociados (fondo y texto) cuando el chip está activo.
 */
@Composable
private fun FilterChipItem(
    text: String, isSelected: Boolean, onClick: () -> Unit, activeColor: CategoryColor
) {
    // Definimos los colores inactivos por defecto
    val inactiveBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val inactiveText = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)

    // Animación suave de colores
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) activeColor.background else inactiveBg,
        animationSpec = tween(durationMillis = 200),
        label = "chipBgColorAnim"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) activeColor.text else inactiveText,
        animationSpec = tween(durationMillis = 200),
        label = "chipTextColorAnim"
    )

    val pillShape = RoundedCornerShape(percent = 50)

    Box(
        modifier = Modifier
            .clip(pillShape)
            .background(color = backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(
                    alpha = 0.4f
                ),
                shape = pillShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}
