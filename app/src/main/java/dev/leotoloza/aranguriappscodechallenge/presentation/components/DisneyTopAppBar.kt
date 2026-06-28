package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

/**
 * Barra de herramientas (TopAppBar) reutilizable y adaptable para la aplicación DisneyApp.
 *
 * Esta barra permite renderizar títulos personalizados y botones de acción. Permite
 * alternar entre una visualización estándar ([TopAppBar]), centrada ([CenterAlignedTopAppBar])
 * y una de tamaño medio ([MediumTopAppBar]) para pantallas detalladas de forma consistente.
 *
 * @param title Contenido Composable para el título de la barra de navegación.
 * @param modifier Modificador para aplicar a la barra.
 * @param navigationIcon Icono o botón que se ubica a la izquierda (ej. botón de regreso).
 * @param actions Conjunto de acciones que se ubican a la derecha.
 * @param isCentered Define si el título debe centrarse en la barra (aplica si no es título mediano).
 * @param isMediumTitle Define si se renderiza con formato de tamaño medio para admitir nombres largos.
 * @param scrollBehavior Comportamiento de desplazamiento asociado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisneyTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    isCentered: Boolean = true,
    isMediumTitle: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    if (isMediumTitle) {
        MediumTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    } else if (isCentered) {
        CenterAlignedTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    } else {
        TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    }
}

/**
 * Sobrecarga conveniente de [DisneyTopAppBar] que acepta un título textual simple.
 *
 * @param titleText Texto del título en formato simple.
 * @param modifier Modificador para aplicar a la barra.
 * @param navigationIcon Icono o botón que se ubica a la izquierda (ej. botón de regreso).
 * @param actions Conjunto de acciones que se ubican a la derecha.
 * @param isCentered Define si el título debe centrarse en la barra (aplica si no es título mediano).
 * @param isMediumTitle Define si se renderiza con formato de tamaño medio para admitir nombres largos.
 * @param scrollBehavior Comportamiento de desplazamiento asociado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisneyTopAppBar(
    titleText: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    isCentered: Boolean = true,
    isMediumTitle: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    DisneyTopAppBar(
        title = {
            Text(
                text = titleText,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        isCentered = isCentered,
        isMediumTitle = isMediumTitle,
        scrollBehavior = scrollBehavior
    )
}

