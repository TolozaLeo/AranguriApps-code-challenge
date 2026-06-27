package dev.leotoloza.aranguriappscodechallenge.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import dev.leotoloza.aranguriappscodechallenge.R

// Configuración del proveedor de Google Fonts
private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Definición de la fuente Plus Jakarta Sans
private val PlusJakartaSansFont = GoogleFont("Plus Jakarta Sans")

val PlusJakartaSansFamily = FontFamily(
    Font(googleFont = PlusJakartaSansFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = PlusJakartaSansFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = PlusJakartaSansFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = PlusJakartaSansFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

// Estilo personalizado para el header en móvil (design.md line 57)
val TitleLargeMobile = TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    color = CharcoalText
)

// Tipografías de Material 3 personalizadas
val Typography = Typography(
    // titleLarge para headers de pantalla y nombres de personajes prominentes (design.md line 51, 105)
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = CharcoalText
    ),
    // bodyMedium para descripciones y metadatos (design.md line 62, 106)
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = CharcoalText
    ),
    // labelSmall para etiquetas de categorías y metadatos secundarios (design.md line 68, 107)
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = CharcoalText
    )
)