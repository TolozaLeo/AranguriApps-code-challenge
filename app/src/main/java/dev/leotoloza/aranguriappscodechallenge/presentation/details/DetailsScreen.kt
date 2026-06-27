package dev.leotoloza.aranguriappscodechallenge.presentation.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    isSinglePane: Boolean = true,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Personaje") },
                navigationIcon = {
                    if (isSinglePane) {
                        IconButton(onClick = onBack) {
                            Text("<") 
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
        ) {
            Text("Details Content")
        }
    }
}