package dev.leotoloza.aranguriappscodechallenge.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import dev.leotoloza.aranguriappscodechallenge.R
import dev.leotoloza.aranguriappscodechallenge.presentation.theme.DisneyCelestialBlue

/**
 * Componente de barra de búsqueda reutilizable y accesible con diseño premium de Disney.
 *
 * Ofrece soporte completo para lectores de pantalla, acciones de teclado (IME Search)
 * y una transición de color de borde personalizada cuando recibe foco.
 *
 * @param query Texto actual ingresado en la barra de búsqueda.
 * @param onQueryChanged Callback que se ejecuta cuando el texto del campo de búsqueda cambia.
 * @param onSearchTriggered Callback que se ejecuta al presionar buscar en el teclado o al pulsar el botón físico de buscar.
 * @param onClearClicked Callback que se ejecuta al hacer clic en el botón de limpiar para restablecer la búsqueda.
 * @param modifier Modificador para aplicar al diseño del contenedor de la barra.
 * @param placeholder Texto de ayuda a mostrar cuando el campo está vacío.
 */
@Composable
fun DisneySearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onClearClicked: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.search_placeholder),
    showSearchButton: Boolean = true
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = { newValue ->
            // Sanitización básica: limitar longitud de búsqueda para prevenir abusos o strings gigantes
            if (newValue.length <= MAX_QUERY_LENGTH) {
                onQueryChanged(newValue)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null, // Icono puramente decorativo
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearClicked,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_search_desc),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (showSearchButton) {
                        IconButton(
                            onClick = {
                                onSearchTriggered(query.trim())
                                keyboardController?.hide()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = stringResource(R.string.search_action_desc),
                                tint = DisneyCelestialBlue
                            )
                        }
                    }
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchTriggered(query.trim())
                keyboardController?.hide()
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DisneyCelestialBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = MaterialTheme.shapes.large
    )
}

/**
 * Longitud máxima permitida para la consulta de búsqueda.
 */
private const val MAX_QUERY_LENGTH = 50
