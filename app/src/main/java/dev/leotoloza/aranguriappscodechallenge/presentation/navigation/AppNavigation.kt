package dev.leotoloza.aranguriappscodechallenge.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.FavoriteCoral
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.FavoriteCoralContainer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.presentation.characters.CharactersScreen
import dev.leotoloza.aranguriappscodechallenge.presentation.characters.CharactersViewModel
import dev.leotoloza.aranguriappscodechallenge.presentation.details.DetailsScreen
import dev.leotoloza.aranguriappscodechallenge.presentation.favorites.FavoritesScreen
import dev.leotoloza.aranguriappscodechallenge.presentation.splash.SplashScreen

/**
 * Saver para serializar/deserializar objetos Character? en el guardado de estado de Compose.
 */
private val CharacterSaver = object : Saver<Character?, Any> {
    override fun SaverScope.save(value: Character?): Any? {
        return if (value == null) null
        else mapOf(
            "id" to value.id,
            "name" to value.name,
            "imageUrl" to value.imageUrl,
            "url" to value.url,
            "films" to ArrayList(value.films),
            "shortFilms" to ArrayList(value.shortFilms),
            "tvShows" to ArrayList(value.tvShows),
            "videoGames" to ArrayList(value.videoGames)
        )
    }

    override fun restore(value: Any): Character? {
        val map = value as? Map<*, *> ?: return null
        return Character(
            id = map["id"] as Int,
            name = map["name"] as String,
            imageUrl = map["imageUrl"] as String,
            url = map["url"] as String,
            films = (map["films"] as List<*>).filterIsInstance<String>(),
            shortFilms = (map["shortFilms"] as List<*>).filterIsInstance<String>(),
            tvShows = (map["tvShows"] as List<*>).filterIsInstance<String>(),
            videoGames = (map["videoGames"] as List<*>).filterIsInstance<String>()
        )
    }
}

/**
 * Componible principal que gestiona la navegación de la aplicación DisneyApp.
 *
 * Coordina los estados y delega el flujo de renderizado y transiciones a sub-componentes
 * especializados para mantener la cohesión y respetar el principio de responsabilidad única (SRP).
 *
 * @param modifier Modificador para aplicar a la estructura de navegación.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val charactersViewModel: CharactersViewModel = hiltViewModel()
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var currentDestination by rememberSaveable { mutableStateOf(BottomNavigation.CHARACTERS) }
    var selectedCharacter by rememberSaveable(stateSaver = CharacterSaver) { mutableStateOf<Character?>(null) }
    val charactersGridState = rememberLazyGridState()

    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 500))
        },
        label = "splashTransition"
    ) { isSplash ->
        if (isSplash) {
            SplashScreen(
                onTimeout = { showSplash = false }
            )
        } else {
            AnimatedDetailsTransition(
                selectedCharacter = selectedCharacter,
                onBack = { selectedCharacter = null },
                modifier = modifier
            ) {
                MainNavigationContent(
                    currentDestination = currentDestination,
                    onDestinationChanged = { destination ->
                        selectedCharacter = null
                        currentDestination = destination
                    },
                    onCharacterClick = { character -> selectedCharacter = character },
                    charactersGridState = charactersGridState,
                    charactersViewModel = charactersViewModel,
                    modifier = modifier
                )
            }
        }
    }
}

/**
 * Wrapper que maneja la animación de deslizamiento y desvanecimiento suave (slide + fade)
 * para la pantalla de detalle, gestionando de forma aislada su BackHandler.
 *
 * @param selectedCharacter Personaje seleccionado a detallar, o null si está en la pantalla principal.
 * @param onBack Callback ejecutado al salir de la vista de detalle.
 * @param modifier Modificador para el contenedor.
 * @param content Contenido a renderizar cuando no hay ningún personaje seleccionado.
 */
@Composable
private fun AnimatedDetailsTransition(
    selectedCharacter: Character?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = selectedCharacter, transitionSpec = {
            if (initialState == null && targetState != null) {
                // Abre el Detalle: se superpone deslizándose de derecha a izquierda con un fade suave
                (slideInHorizontally(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    initialOffsetX = { it }) + fadeIn(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )) togetherWith (slideOutHorizontally(
                    animationSpec = tween(
                    durationMillis = 400, easing = FastOutSlowInEasing
                ),
                    targetOffsetX = { -it / 3 } // Desplaza la pantalla origen ligeramente a la izquierda
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                ))
            } else {
                // Cierra el Detalle: se retira deslizándose de izquierda a derecha con un fade suave
                (slideInHorizontally(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    initialOffsetX = { -it / 3 }) + fadeIn(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )) togetherWith (slideOutHorizontally(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    targetOffsetX = { it }) + fadeOut(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                ))
            }
        }, label = "detailsScreenTransition"
    ) { character ->
        if (character != null) {
            // Intercepta el gesto de retroceso del sistema en la pantalla de detalle
            BackHandler(enabled = true, onBack = onBack)
            DetailsScreen(
                character = character,
                onBack = onBack,
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        } else {
            content()
        }
    }
}

/**
 * Contenedor principal que estructura el menú inferior adaptativo (NavigationSuiteScaffold)
 * y aloja el contenido de las pestañas principales.
 *
 * @param currentDestination Destino raíz activo en la barra de navegación.
 * @param onDestinationChanged Callback para actualizar el destino seleccionado.
 * @param onCharacterClick Callback ejecutado al seleccionar un personaje de la lista.
 * @param charactersGridState Estado de scroll de la grilla de personajes.
 * @param charactersViewModel El ViewModel pre-instanciado para la pantalla de personajes.
 * @param modifier Modificador para el contenedor.
 */
@Composable
private fun MainNavigationContent(
    currentDestination: BottomNavigation,
    onDestinationChanged: (BottomNavigation) -> Unit,
    onCharacterClick: (Character) -> Unit,
    charactersGridState: LazyGridState,
    charactersViewModel: CharactersViewModel,
    modifier: Modifier = Modifier
) {
    // Configura el manejador de retroceso según la pantalla activa
    if (currentDestination == BottomNavigation.FAVORITES) {
        BackHandler(enabled = true) {
            // Si está en Favoritos, al presionar atrás regresa a la pantalla de Personajes
            onDestinationChanged(BottomNavigation.CHARACTERS)
        }
    } else {
        BackHandler(enabled = true) {
            // Si está en Personajes (la raíz), el retroceso no realiza acción para evitar cerrar la app abruptamente
        }
    }

    val charactersItemColors = rememberNavigationSuiteItemColors(isFavorites = false)
    val favoritesItemColors = rememberNavigationSuiteItemColors(isFavorites = true)

    NavigationSuiteScaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.background,
            navigationRailContainerColor = MaterialTheme.colorScheme.background
        ),
        navigationSuiteItems = {
            BottomNavigation.entries.forEach { destination ->
                val isSelected = currentDestination == destination
                val isFavorites = destination == BottomNavigation.FAVORITES
                val itemColors = if (isFavorites) favoritesItemColors else charactersItemColors
                item(
                    selected = isSelected,
                    onClick = { onDestinationChanged(destination) },
                    icon = {
                        NavigationSuiteItemLabel(
                            destination = destination,
                            isSelected = isSelected,
                            selectedColor = if (isFavorites) FavoriteCoral else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    colors = itemColors
                )
            }
        }) {
        AnimatedTabContent(
            currentDestination = currentDestination,
            charactersGridState = charactersGridState,
            onCharacterClick = onCharacterClick,
            charactersViewModel = charactersViewModel
        )
    }
}

/**
 * Componente visual que representa el ícono y la etiqueta textual de cada elemento de la barra de navegación.
 *
 * @param destination Destino de navegación representado por el item.
 * @param isSelected Define si la pestaña asociada está activa.
 * @param modifier Modificador para aplicar al contenedor.
 * @param selectedColor Color utilizado para el icono y el texto cuando la pestaña está seleccionada.
 */
@Composable
private fun NavigationSuiteItemLabel(
    destination: BottomNavigation,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
            contentDescription = stringResource(destination.titleResId),
            tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(destination.titleResId),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Maneja la transición animada deslizante entre las pestañas a nivel raíz.
 *
 * @param currentDestination Destino actual que se desea mostrar.
 * @param charactersGridState Estado de scroll de la grilla de personajes.
 * @param onCharacterClick Callback ejecutado al seleccionar un personaje.
 * @param charactersViewModel El ViewModel pre-instanciado para la pantalla de personajes.
 * @param modifier Modificador para el contenedor.
 */
@Composable
private fun AnimatedTabContent(
    currentDestination: BottomNavigation,
    charactersGridState: LazyGridState,
    onCharacterClick: (Character) -> Unit,
    charactersViewModel: CharactersViewModel,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = currentDestination, transitionSpec = {
            val initialIndex = initialState.ordinal
            val targetIndex = targetState.ordinal
            if (targetIndex > initialIndex) {
                // Hacia la derecha (Favoritos): entra de derecha a izquierda, sale hacia la izquierda
                slideInHorizontally(
                    animationSpec = tween(durationMillis = 300),
                    initialOffsetX = { it }) togetherWith slideOutHorizontally(
                    animationSpec = tween(durationMillis = 300), targetOffsetX = { -it })
            } else {
                // Hacia la izquierda (Personajes): entra de izquierda a derecha, sale hacia la derecha
                slideInHorizontally(
                    animationSpec = tween(durationMillis = 300),
                    initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                    animationSpec = tween(durationMillis = 300), targetOffsetX = { it })
            }
        }, label = "tabTransition", modifier = modifier
    ) { destination ->
        when (destination) {
            BottomNavigation.CHARACTERS -> {
                CharactersScreen(
                    gridState = charactersGridState,
                    onCharacterClick = onCharacterClick,
                    viewModel = charactersViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            BottomNavigation.FAVORITES -> {
                FavoritesScreen(
                    onCharacterClick = onCharacterClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }
    }
}

/**
 * Inicializa y memoriza los colores configurados para los elementos activos/inactivos del menú de navegación.
 *
 * @param isFavorites Define si se están configurando los colores para el ítem de favoritos.
 * @return Los colores para los componentes de barra y riel de la suite de navegación.
 */
@Composable
private fun rememberNavigationSuiteItemColors(isFavorites: Boolean) = NavigationSuiteDefaults.itemColors(
    navigationBarItemColors = NavigationBarItemDefaults.colors(
        indicatorColor = if (isFavorites) FavoriteCoralContainer else MaterialTheme.colorScheme.secondaryContainer,
        selectedIconColor = if (isFavorites) FavoriteCoral else MaterialTheme.colorScheme.onSecondaryContainer,
        selectedTextColor = if (isFavorites) FavoriteCoral else MaterialTheme.colorScheme.onSecondaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    ), navigationRailItemColors = NavigationRailItemDefaults.colors(
        indicatorColor = if (isFavorites) FavoriteCoralContainer else MaterialTheme.colorScheme.secondaryContainer,
        selectedIconColor = if (isFavorites) FavoriteCoral else MaterialTheme.colorScheme.onSecondaryContainer,
        selectedTextColor = if (isFavorites) FavoriteCoral else MaterialTheme.colorScheme.onSecondaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
)
