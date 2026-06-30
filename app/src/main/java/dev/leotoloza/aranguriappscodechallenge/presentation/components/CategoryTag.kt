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
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.CategoryColor

/**
 * Categorías de medios en los que puede aparecer un personaje de Disney.
 *
 * Cada entrada define la etiqueta de presentación (en español) que se muestra
 * en los tags de la UI. Los colores se resuelven en tiempo de composición
 * mediante [categoryColor].
 *
 * @property label Texto que se muestra en el tag de categoría.
 */
enum class CharacterCategory(val label: String) {
    /** Cortometraje */
    SHORT_FILM("Corto"),

    /** Serie de televisión */
    TV_SHOW("ShowTv"),

    /** Videojuego */
    VIDEO_GAME("Juego"),

    /** Película */
    FILM("Pelicula");
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

/**
 * Devuelve la lista de categorías activas (con al menos un ítem) para este personaje.
 * Solo incluye las categorías en las que el personaje tiene presencia real.
 */
fun Character.activeCategories(): List<CharacterCategory> = buildList {
    if (films.isNotEmpty()) add(CharacterCategory.FILM)
    if (shortFilms.isNotEmpty()) add(CharacterCategory.SHORT_FILM)
    if (tvShows.isNotEmpty()) add(CharacterCategory.TV_SHOW)
    if (videoGames.isNotEmpty()) add(CharacterCategory.VIDEO_GAME)
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
