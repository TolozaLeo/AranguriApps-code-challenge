package dev.leotoloza.aranguriappscodechallenge.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ==========================================
// Estructuras de Diseño Personalizadas
// ==========================================

/**
 * Representa la escala de espaciado definida para la aplicación (design.md line 81-86)
 */
data class AppSpacing(
    val marginPage: Dp = 16.dp, // 1rem
    val gutter: Dp = 16.dp,     // 1rem
    val stackSm: Dp = 8.dp,     // 0.5rem ( internal grouping)
    val stackMd: Dp = 16.dp,    // 1rem (default spacing)
    val stackLg: Dp = 24.dp     // 1.5rem (spacing between unrelated elements)
)

/**
 * Representa el par de color base y glow para una categoría (design.md line 99)
 */
data class CategoryColor(
    val base: Color,
    val glow: Color
)

/**
 * Contiene los colores temáticos de las categorías de personajes de la aplicación (design.md line 99)
 */
data class AppCategoryColors(
    val film: CategoryColor = CategoryColor(CategoryFilmBase, CategoryFilmGlow),
    val tvShow: CategoryColor = CategoryColor(CategoryTvBase, CategoryTvGlow),
    val shortFilm: CategoryColor = CategoryColor(CategoryShortBase, CategoryShortGlow),
    val videoGame: CategoryColor = CategoryColor(CategoryGameBase, CategoryGameGlow),
    val parkAttraction: CategoryColor = CategoryColor(CategoryParkBase, CategoryParkGlow)
)

// Provisiones locales para CompositionLocal
val LocalSpacing = staticCompositionLocalOf { AppSpacing() }
val LocalCategoryColors = staticCompositionLocalOf { AppCategoryColors() }

// ==========================================
// Extensiones en MaterialTheme
// ==========================================

/**
 * Acceso directo al espaciado personalizado de la app
 */
val MaterialTheme.spacing: AppSpacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

/**
 * Acceso directo a los colores de categoría personalizados de la app
 */
val MaterialTheme.categoryColors: AppCategoryColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCategoryColors.current

// ==========================================
// Definición de Configuración de M3
// ==========================================

// Configuración de Formas (design.md line 127-134)
val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),      // Input Fields (8px/dp)
    medium = RoundedCornerShape(12.dp),    // Cards (12px/dp)
    large = RoundedCornerShape(24.dp)      // Search Pill Shape (24px/dp)
)

// Esquema de Colores Oscuro personalizado (Adaptado en base a Celestial Blue)
private val DarkColorScheme = darkColorScheme(
    primary = InversePrimary,
    onPrimary = Color(0xFF00344B),
    primaryContainer = Color(0xFF004C6B),
    onPrimaryContainer = Color(0xFFC6E7FF),
    secondary = Color(0xFF9ACDEC),
    onSecondary = Color(0xFF0F4C66),
    secondaryContainer = Color(0xFF2E637E),
    onSecondaryContainer = Color(0xFFC3E8FF),
    tertiary = Color(0xFFFFB95B),
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF643F00),
    onTertiaryContainer = Color(0xFFFFDDB7),
    background = Color(0xFF191C1E),
    onBackground = Color(0xFFE2E2E5),
    surface = Color(0xFF191C1E),
    onSurface = Color(0xFFE2E2E5),
    surfaceVariant = Color(0xFF41474D),
    onSurfaceVariant = Color(0xFFC0C7CD),
    outline = Color(0xFF8A9297),
    outlineVariant = Color(0xFF41474D),
    inverseSurface = Color(0xFFE2E2E5),
    inverseOnSurface = Color(0xFF2F3033),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Esquema de Colores Claro según la especificación exacta de design.md
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    surfaceTint = Color(0xFF00658D)
)

@Composable
fun AranguriAppsCodeChallengeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Deshabilitado por defecto para dar prioridad al diseño Celestial Blue y pristine white
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalSpacing provides AppSpacing(),
        LocalCategoryColors provides AppCategoryColors()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}