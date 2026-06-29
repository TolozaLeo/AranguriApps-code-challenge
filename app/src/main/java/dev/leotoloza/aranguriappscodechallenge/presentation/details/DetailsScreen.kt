package dev.leotoloza.aranguriappscodechallenge.presentation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneyAsyncImage
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneyTopAppBar
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme

/**
 * Pantalla de detalle que muestra la información extendida de un personaje de Disney real.
 *
 * Presenta una imagen destacada del personaje, el listado de producciones en las que aparece
 * agrupado por categorías (películas, cortos, shows de TV, videojuegos), y cuenta con
 * comportamiento adaptable y soporte para regresar a la pantalla anterior.
 *
 * @param character El objeto [Character] real del personaje a detallar.
 * @param isSinglePane Indica si la pantalla se muestra de forma individual (ocupa todo el ancho de pantalla).
 * @param onBack Callback que se ejecuta al presionar el botón de regresar.
 * @param modifier Modificador para aplicar al diseño de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    character: Character,
    isSinglePane: Boolean = true,
    onBack: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        topBar = {
            DisneyTopAppBar(
                titleText = character.name,
                isCentered = true,
                isMediumTitle = false,
                navigationIcon = {
                    if (isSinglePane) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = AppTheme.colors.primary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Imagen de portada del personaje de ancho completo usando el componente de imagen común
            DisneyAsyncImage(
                imageUrl = character.imageUrl,
                contentDescription = "Imagen de ${character.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.stackMd))

            // Cabecera de contenido con línea divisoria integrada a la derecha
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.spacing.marginPage),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aparece en:",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = AppTheme.colors.outlineVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Construir lista de categorías activas para renderizado condicional ordenadas
            val activeSections = buildList {
                if (character.films.isNotEmpty()) {
                    add(
                        Triple(
                            "Películas",
                            Icons.Default.Movie,
                            character.films
                        )
                    )
                }
                if (character.tvShows.isNotEmpty()) {
                    add(
                        Triple(
                            "Programas de TV",
                            Icons.Default.Tv,
                            character.tvShows
                        )
                    )
                }
                if (character.shortFilms.isNotEmpty()) {
                    add(
                        Triple(
                            "Cortometrajes",
                            Icons.Default.Slideshow,
                            character.shortFilms
                        )
                    )
                }
                if (character.videoGames.isNotEmpty()) {
                    add(
                        Triple(
                            "Videojuegos",
                            Icons.Default.SportsEsports,
                            character.videoGames
                        )
                    )
                }
            }

            activeSections.forEachIndexed { sectionIndex, (title, icon, items) ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cabecera de la Categoría con su icono de color azul
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.spacing.marginPage),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = AppTheme.colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = AppTheme.colors.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Elementos de la categoría
                    items.forEachIndexed { itemIndex, item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppTheme.colors.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = AppTheme.spacing.marginPage,
                                    vertical = 12.dp
                                )
                        )
                        if (itemIndex < items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AppTheme.spacing.marginPage),
                                color = AppTheme.colors.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                if (sectionIndex < activeSections.lastIndex) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
