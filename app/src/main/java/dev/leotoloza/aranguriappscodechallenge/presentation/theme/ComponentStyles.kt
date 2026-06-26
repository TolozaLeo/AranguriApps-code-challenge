package dev.leotoloza.aranguriappscodechallenge.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.colors: ColorScheme
    get() = LocalAppColorScheme.currentValue

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.appShapes: Shapes
    get() = LocalAppShapes.currentValue

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.categoryColors: AppCategoryColors
    get() = LocalCategoryColors.currentValue

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.spacing: AppSpacing
    get() = LocalSpacing.currentValue

@OptIn(ExperimentalFoundationStyleApi::class)
object AppStyles {
    val characterCardStyle = Style {
        shape(appShapes.medium)
        background(colors.surfaceContainerHighest ?: colors.surfaceVariant) // surfaceContainerHighest might not exist, use surfaceVariant
    }

    val tagBaseStyle = Style {
        // Border and glow will be applied later
    }
}
