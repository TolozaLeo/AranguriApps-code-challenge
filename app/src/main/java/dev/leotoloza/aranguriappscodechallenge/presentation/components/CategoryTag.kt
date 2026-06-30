package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterCategory
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.CategoryColor

/**
 * Obtiene la etiqueta amigable en español correspondiente para mostrar en el tag.
 */
val CharacterCategory.label: String
    get() = when (this) {
        CharacterCategory.SHORT_FILM -> "Corto"
        CharacterCategory.TV_SHOW -> "ShowTv"
        CharacterCategory.VIDEO_GAME -> "Juego"
        CharacterCategory.FILM -> "Pelicula"
    }

/**
 * Resuelve el par de colores del tema para esta categoría.
 */
val CharacterCategory.categoryColor: CategoryColor
    @Composable
    get() = when (this) {
        CharacterCategory.SHORT_FILM -> AppTheme.categoryColors.shortFilm
        CharacterCategory.TV_SHOW -> AppTheme.categoryColors.tvShow
        CharacterCategory.VIDEO_GAME -> AppTheme.categoryColors.videoGame
        CharacterCategory.FILM -> AppTheme.categoryColors.film
    }


/** Forma de píldora reutilizada para el tag de categoría. */
private val PillShape = RoundedCornerShape(percent = 50)

/**
 * Tag reutilizable con forma de píldora en tonos pasteles de alto contraste.
 *
 * Renderiza un tag compacto con un fondo pastel liso y texto en un tono más oscuro
 * para asegurar legibilidad conforme a las pautas WCAG.
 *
 * @param text Texto de la etiqueta a mostrar dentro del tag.
 * @param colors Par de colores (fondo y texto) que definen la estética del tag.
 * @param modifier Modificador para aplicar al tag.
 */
@Composable
fun CategoryTag(
    text: String,
    colors: CategoryColor,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = colors.background,
                shape = PillShape
            )
            .border(
                width = 1.dp,
                color = colors.text.copy(alpha = 0.2f),
                shape = PillShape
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colors.text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp
        )
    }
}
