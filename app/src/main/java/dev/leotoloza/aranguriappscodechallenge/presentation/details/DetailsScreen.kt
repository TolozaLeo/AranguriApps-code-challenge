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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.leotoloza.aranguriappscodechallenge.presentation.components.DisneyTopAppBar
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.AppTheme

/**
 * Pantalla de detalle que muestra la información extendida de un personaje de Disney.
 *
 * Presenta una imagen destacada del personaje, el listado de producciones en las que aparece
 * agrupado por categorías (películas, cortos, shows de TV, videojuegos), y cuenta con
 * comportamiento adaptable y soporte para regresar a la pantalla anterior.
 *
 * @param characterName Nombre del personaje del cual se mostrarán los detalles.
 * @param isSinglePane Indica si la pantalla se muestra de forma individual (ocupa todo el ancho de pantalla).
 * @param onBack Callback que se ejecuta al presionar el botón de regresar.
 * @param modifier Modificador para aplicar al diseño de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    characterName: String,
    isSinglePane: Boolean = true,
    onBack: () -> Unit = {},
) {
    val detail = getMockCharacterDetail(characterName)
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        topBar = {
            DisneyTopAppBar(
                titleText = characterName,
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
            // Imagen de portada del personaje de ancho completo
            AsyncImage(
                model = detail.imageUrl,
                contentDescription = "Imagen de $characterName",
                contentScale = ContentScale.Crop,
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
                if (detail.films.isNotEmpty()) {
                    add(
                        Triple(
                            "Películas",
                            Icons.Default.Movie,
                            detail.films
                        )
                    )
                }
                if (detail.tvShows.isNotEmpty()) {
                    add(
                        Triple(
                            "Programas de TV",
                            Icons.Default.Tv,
                            detail.tvShows
                        )
                    )
                }
                if (detail.shortFilms.isNotEmpty()) {
                    add(
                        Triple(
                            "Cortometrajes",
                            Icons.Default.Slideshow,
                            detail.shortFilms
                        )
                    )
                }
                if (detail.videoGames.isNotEmpty()) {
                    add(
                        Triple(
                            "Videojuegos",
                            Icons.Default.SportsEsports,
                            detail.videoGames
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

/**
 * Representa los detalles ficticios/mockeados del personaje para la visualización del diseño.
 */
private data class MockCharacterDetail(
    val name: String,
    val imageUrl: String,
    val films: List<String>,
    val tvShows: List<String>,
    val shortFilms: List<String>,
    val videoGames: List<String>
)

/**
 * Provee datos mockeados simulados para el personaje seleccionado.
 *
 * Utiliza el nombre para variar la disponibilidad de categorías y verificar
 * el renderizado condicional en la pantalla de detalle, replicando los datos
 * de la imagen de referencia.
 */
private fun getMockCharacterDetail(name: String): MockCharacterDetail {
    return MockCharacterDetail(
        name = name,
        imageUrl = "https://picsum.photos/seed/${name.hashCode()}/800/400",
        films = listOf(
            "Fantasia (1940)",
            "Mickey, Donald, Goofy: The Three Musketeers",
            "Saving Mr. Banks"
        ),
        tvShows = listOf(
            "Mickey Mouse Clubhouse",
            "The Wonderful World of Mickey Mouse"
        ),
        shortFilms = listOf(
            "Steamboat Willie (1928)",
            "The Band Concert"
        ),
        videoGames = listOf(
            "Kingdom Hearts Series"
        )
    )
}
