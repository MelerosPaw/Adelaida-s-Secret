package com.example.composetest.ui.compose.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
//region Adelaida
private val Fuerte = Color(0x88342020)
private val Primario = Color(0xFF342020)
private val Fondo = Color(0xFF5D4B4B)

// region Fondo de pantalla
val FondoPantallaClaro = Color(0xFF947D7D)
val FondoPantallaOscuro = Color(0xFF5D4B4B)
val FondoPantallaVerdeClaro = Color(0xFF759189)
val FondoPantallaVerdeOscuro = Color(0xFF506962)
// endregion

// region Campo de texto
val FondoCampoTextoClaro = Color(0xFF7B6767)
val FondoCampoTextoOscuro = Color(0xFF806969)
val FondoCampoTextoVerdeClaro = Color(0xFF666D63)
val FondoCampoTextoVerdeOscuro = Color(0xFF6D8069)
val ErrorCampoTextoClaro = Color(0xFF670C0C)
val ErrorCampoTextoOscuro = Color(0xFFDC4141)
val ErrorCampoTextoVerdeClaro = Color(0xFF175901)
val ErrorCampoTextoVerdeOscuro = Color(0xFF63DC41)
val FondoCampoTextoSeleccionadoClaro = Color(0xFFB59696)
val FondoCampoTextoSeleccionadoOscuro = Color(0xFFD1BEBE)
val FondoCampoTextoSeleccionadoVerdeClaro = Color(0xFF9EB596)
val FondoCampoTextoSeleccionadoVerdeOscuro = Color(0xFFC0D1BE)
val TextoCampoTextoClaro = Color(0xFF342020)
val TextoCampoTextoOscuro = Color(0xFF342020)
val TextoCampoTextoVerdeClaro = Color(0xFF253420)
val TextoCampoTextoVerdeOscuro = Color(0xFF253420)
// endregion

val TextoPresionado = Color(0xFF7C7C7C)
val FondoBoton = Fuerte
val Explicacion = Fuerte

// region Botones
val RellenoBotonClaro = Color(0xFFBB9898)
val RellenoBotonOscuro = Color(0xFF342020)
val RellenoBotonVerdeClaro = Color(0xFFB4EDDD)
val RellenoBotonVerdeOscuro = Color(0xFF242F2C)
val TextoBotonClaro = Color(0xFF211B1B)
val TextoBotonOscuro = Color(0xFFDBDBDB)
val TextoBotonVerdeClaro = Color(0xFF272E25)
val TextoBotonVerdeOscuro = Color(0xFFDBDBDB)
val TextoBotonInhabilitadoClaro = Color(0xFFA4908B)
val TextoBotonInhabilitadoOscuro = Color(0xFF836E6E)
val TextoBotonInhabilitadoVerdeClaro = Color(0xFF909561)
val TextoBotonInhabilitadoVerdeOscuro = Color(0xFF878787)
val RellenoBotonPulsadoClaro = Color(0xFF7B5F5F)
val RellenoBotonPulsadoOscuro = Color(0xFF403232)
val RellenoBotonPulsadoVerdeClaro = Color(0xFF617B5F)
val RellenoBotonPulsadoVerdeOscuro = Color(0xFF364032)
val DecoradoBotonClaro = Color(0x49FFFFFF)
val DecoradoBotonOscuro = Color(0x33DCC89A)
val DecoradoBotonVerdeClaro = Color(0xFF2F412B)
// endregion

// region Texto
val TextoOscuro = Color(0xFFDBDBDB)
val TextoClaro = Color(0xFF392623)
val TextoVerdeOscuro = Color(0xFFDBDBDB)
val TextoVerdeClaro = Color(0xFF161D14)
val TextoInhabilitadoClaro = Color(0xFF555555)
val TextoInhabilitadoOscuro = Color(0xFF555555)
val TextoInhabilitadoVerdeClaro = Color(0xFF555555)
val TextoInhabilitadoVerdeOscuro = Color(0xFF555555)
// endregion

// region Iconos
val IconosClaro = TextoClaro
val IconosOscuro = TextoOscuro
val IconosVerdeClaro = TextoVerdeClaro
val IconosVerdeOscuro = TextoVerdeOscuro
// endergion

//region Checkbox
val CheckboxMarcadoClaro = Color(0x88342020)
val CheckboxMarcadoOscuro = Color(0x88342020)
val CheckboxMarcadoVerdeClaro = Color(0x88293420)
val CheckboxMarcadoVerdeOscuro = Color(0x88293420)
val CheckboxMarcaClaro = TextoOscuro
val CheckboxMarcaOscuro = TextoOscuro
val CheckboxMarcaVerdeClaro = TextoClaro
val CheckboxMarcaVerdeOscuro = TextoVerdeOscuro
val CheckboxDesmarcadoClaro = TextoClaro
val CheckboxDesmarcadoOscuro = TextoOscuro
val CheckboxDesmarcadoVerdeClaro = TextoVerdeClaro
val CheckboxDesmarcadoVerdeOscuro = TextoVerdeOscuro
//endregion

// region Dialogs
val RellenoDialogoClaro = Color(0xBEF4F4F4)
val RellenoDialogoOscuro = Color(0xBE000000)
val ContenidoDialogoClaro = Color(0xFF121212)
val ContenidoDialogoOscuro = Color(0xFFF4F4F4)
val ContenidoInhabilitadoDialogoClaro = Color(0xFF9D9D9D)
val ContenidoInhabilitadoDialogoOscuro = Color(0xFF676767)
// endregion

val FondoPantallaRobo = Color(0xFFBEA079)
val FondoTarjetaSospechoso = Color(0x11BEA079)
val FondoListasClaro = Color(0x33362323)
val FondoListasOscuro = Color(0x33362323)
val FondoListasVerdeClaro = Color(0x332D3623)
val FondoListasVerdeOscuro = Color(0x33283623)
val FondoPantallaCargando = Color(0x995D4B4B)
val TextoNormal = Color.White
val TextoDialogOutlinedTextFieldUnfocused = Color(0x995D4B4B)
val TextoDialogOutlinedTextField = Color.White
val DivisorHorizontalClaro = Color(0xD5DCC89A)
val DivisorHorizontalOscuro = Color(0x33DCC89A)
val Hueco = Color(0xFF403232)
val ContenedorCartas = Color(0xFF675454)
val FondoBaremoSeleccionado = Primario
val FondoBaremoSinSeleccionar = Color.White
val TextoBaremoSeleccionado = Color.White
val TextoBaremoSinSeleccionar = Primario
val ResalteOscuro = Color(0xFFDFB47A)
val ResalteClaro = Color(0xFFDFC7A6)
val ResalteVerdeOscuro = Color(0xFFC1CB91)
val ResalteVerdeClaro = Color(0xFFBBC781)
// endregion

val ToolbarClaro = Color(0xFF634C4C)
val ToolbarOscuro = Color(0xFF342020)
val ToolbarVerdeClaro = Color(0xFF4A7C71)
val ToolbarVerdeOscuro = Color(0xFF2A3835)
val BarraEstado = Color(0xFF231515)
val BarraEstadoVerde = Color(0xFF111F1C)
val IndicadorSeleccionBarraNavegacionClaro = Color.White
val IndicadorSeleccionadoBarraNavegacionOscuro = Color(0xFF160E0E)
val IndicadorSeleccionadoBarraNavegacionVerdeClaro = Color.White
val IndicadorSeleccionadoBarraNavegacionVerdeOscuro = Color(0xFF263420)
//endregion

@Composable
fun adelaidaColorScheme(useDynamicColor: Boolean, darkTheme: Boolean): ColorScheme = when {
  canUseDynamicColor(useDynamicColor) -> dynamicScheme(darkTheme)
  darkTheme -> DarkScheme
  else -> LightScheme
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun dynamicScheme(darkTheme: Boolean): ColorScheme = LocalContext.current.let {
  if (darkTheme) dynamicDarkColorScheme(it) else dynamicLightColorScheme(it)
}

@Immutable
data class TokensColores(
  val fondoPantalla: Color,
  val statusBar: Color,
  val toolbarContainer: Color,
  val toolbarContent: Color,
  val texto: Color,
  val tituloInicioRonda: Color,
  val subtituloInicioRonda: Color,
  val rellenoBoton: Color,
  val rellenoBotonPulsado: Color,
  val contenidoBoton: Color,
  val textoBotonInhabilitado: Color,
  val decoradoBoton: Color,
  val resalte: Color,
  val fondoListas: Color,
  val iconos: Color,
  val fondoCampoTexto: Color,
  val fondoCampoTextoSeleccionado: Color,
  val errorCampoTexto: Color,
  val indicadorSeleccionBarraNavegacion: Color,
  val iconoBarraNavegacion: Color,
  val iconoBarraNavegacionSelecionado: Color,
  val fondoDialogos: Color,
  val contenidoDialogos: Color,
  val contenidoDialogosInhabilitado: Color,
  val textoCuadroTexto: Color,
  val fondoCheckboxMarcado: Color,
  val fondoCheckboxDesmarcado: Color,
  val fondoCheckboxInhabilitado: Color,
  val marcaCheckbox: Color,
  val divisor: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
  TokensColores(
    fondoPantalla = Color.Unspecified,
    statusBar = Color.Unspecified,
    toolbarContainer = Color.Unspecified,
    toolbarContent = Color.Unspecified,
    texto = Color.Unspecified,
    tituloInicioRonda = Color.Unspecified,
    subtituloInicioRonda = Color.Unspecified,
    rellenoBoton = Color.Unspecified,
    rellenoBotonPulsado = Color.Unspecified,
    contenidoBoton = Color.Unspecified,
    textoBotonInhabilitado = Color.Unspecified,
    decoradoBoton = Color.Unspecified,
    resalte = Color.Unspecified,
    fondoListas = Color.Unspecified,
    iconos = Color.Unspecified,
    fondoCampoTexto = Color.Unspecified,
    fondoCampoTextoSeleccionado = Color.Unspecified,
    errorCampoTexto = Color.Unspecified,
    indicadorSeleccionBarraNavegacion = Color.Unspecified,
    fondoDialogos = Color.Unspecified,
    contenidoDialogos = Color.Unspecified,
    contenidoDialogosInhabilitado = Color.Unspecified,
    textoCuadroTexto = Color.Unspecified,
    iconoBarraNavegacion = Color.Unspecified,
    iconoBarraNavegacionSelecionado = Color.Unspecified,
    fondoCheckboxMarcado = Color.Unspecified,
    fondoCheckboxDesmarcado = Color.Unspecified,
    fondoCheckboxInhabilitado = Color.Unspecified,
    marcaCheckbox = Color.Unspecified,
    divisor = Color.Unspecified,
  )
}

// region Light
val primaryLight = Color(0xFF342020)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFDDB9)
val onPrimaryContainerLight = Color(0xFF663E00)
val secondaryLight = Color(0xFF715A41)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFDDDBD)
val onSecondaryContainerLight = Color(0xFF58432B)
val tertiaryLight = Color(0xFF835414)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFDDB9)
val onTertiaryContainerLight = Color(0xFF663E00)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFFFF8F4)
val onBackgroundLight = Color(0xFF211A13)
val surfaceLight = Color(0xFF796767)
val onSurfaceLight = Color(0xFF211A13)
val surfaceVariantLight = Color(0xFFF1E0D0)
val onSurfaceVariantLight = Color(0xFF504539)
val outlineLight = Color(0xFF827568)
val outlineVariantLight = Color(0xFFD4C4B5)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF372F27)
val inverseOnSurfaceLight = Color(0xFFFCEEE2)
val inversePrimaryLight = Color(0xFFF9BB71)
val surfaceDimLight = Color(0xFFE5D8CC)
val surfaceBrightLight = Color(0xFFFFF8F4)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1E6)
val surfaceContainerLight = Color(0xFFFAEBE0)
val surfaceContainerHighLight = Color(0xFFF4E6DA)
val surfaceContainerHighestLight = Color(0xFFEEE0D5)
// endregion

//region Dark
val primaryDark = Color(0xFFF9BB71)
val onPrimaryDark = Color(0xFF472A00)
val primaryContainerDark = Color(0xFF663E00)
val onPrimaryContainerDark = Color(0xFFFFDDB9)
val secondaryDark = Color(0xFFDFC1A2)
val onSecondaryDark = Color(0xFF3F2D17)
val secondaryContainerDark = Color(0xFF58432B)
val onSecondaryContainerDark = Color(0xFFFDDDBD)
val tertiaryDark = Color(0xFFF9BB71)
val onTertiaryDark = Color(0xFF472A00)
val tertiaryContainerDark = Color(0xFF663E00)
val onTertiaryContainerDark = Color(0xFFFFDDB9)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF18120C)
val onBackgroundDark = Color(0xFFEEE0D5)
val surfaceDark = FondoPantallaClaro
val onSurfaceDark = Color(0xFFEEE0D5)
val surfaceVariantDark = Color(0xFF504539)
val onSurfaceVariantDark = Color(0xFFD4C4B5)
val outlineDark = Color(0xFF9D8E81)
val outlineVariantDark = Color(0xFF504539)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFEEE0D5)
val inverseOnSurfaceDark = Color(0xFF372F27)
val inversePrimaryDark = Color(0xFF835414)
val surfaceDimDark = Color(0xFF18120C)
val surfaceBrightDark = Color(0xFF403830)
val surfaceContainerLowestDark = Color(0xFF130D07)
val surfaceContainerLowDark = Color(0xFF211A13)
val surfaceContainerDark = Color(0xFF251E17)
val surfaceContainerHighDark = Color(0xFF302921)
val surfaceContainerHighestDark = Color(0xFF3B332C)
//endregion

//region Schemes
val LightScheme = lightColorScheme(
  primary = primaryLight,
  onPrimary = onPrimaryLight,
  primaryContainer = primaryContainerLight,
  onPrimaryContainer = onPrimaryContainerLight,
  secondary = secondaryLight,
  onSecondary = onSecondaryLight,
  secondaryContainer = secondaryContainerLight,
  onSecondaryContainer = onSecondaryContainerLight,
  tertiary = tertiaryLight,
  onTertiary = onTertiaryLight,
  tertiaryContainer = tertiaryContainerLight,
  onTertiaryContainer = onTertiaryContainerLight,
  error = errorLight,
  onError = onErrorLight,
  errorContainer = errorContainerLight,
  onErrorContainer = onErrorContainerLight,
  background = backgroundLight,
  onBackground = onBackgroundLight,
  surface = surfaceLight,
  onSurface = onSurfaceLight,
  surfaceVariant = surfaceVariantLight,
  onSurfaceVariant = onSurfaceVariantLight,
  outline = outlineLight,
  outlineVariant = outlineVariantLight,
  scrim = scrimLight,
  inverseSurface = inverseSurfaceLight,
  inverseOnSurface = inverseOnSurfaceLight,
  inversePrimary = inversePrimaryLight,
  surfaceDim = surfaceDimLight,
  surfaceBright = surfaceBrightLight,
  surfaceContainerLowest = surfaceContainerLowestLight,
  surfaceContainerLow = surfaceContainerLowLight,
  surfaceContainer = surfaceContainerLight,
  surfaceContainerHigh = surfaceContainerHighLight,
  surfaceContainerHighest = surfaceContainerHighestLight,
)

val DarkScheme = darkColorScheme(
  primary = primaryDark,
  onPrimary = onPrimaryDark,
  primaryContainer = primaryContainerDark,
  onPrimaryContainer = onPrimaryContainerDark,
  secondary = secondaryDark,
  onSecondary = onSecondaryDark,
  secondaryContainer = secondaryContainerDark,
  onSecondaryContainer = onSecondaryContainerDark,
  tertiary = tertiaryDark,
  onTertiary = onTertiaryDark,
  tertiaryContainer = tertiaryContainerDark,
  onTertiaryContainer = onTertiaryContainerDark,
  error = errorDark,
  onError = onErrorDark,
  errorContainer = errorContainerDark,
  onErrorContainer = onErrorContainerDark,
  background = backgroundDark,
  onBackground = onBackgroundDark,
  surface = surfaceDark,
  onSurface = onSurfaceDark,
  surfaceVariant = surfaceVariantDark,
  onSurfaceVariant = onSurfaceVariantDark,
  outline = outlineDark,
  outlineVariant = outlineVariantDark,
  scrim = scrimDark,
  inverseSurface = inverseSurfaceDark,
  inverseOnSurface = inverseOnSurfaceDark,
  inversePrimary = inversePrimaryDark,
  surfaceDim = surfaceDimDark,
  surfaceBright = surfaceBrightDark,
  surfaceContainerLowest = surfaceContainerLowestDark,
  surfaceContainerLow = surfaceContainerLowDark,
  surfaceContainer = surfaceContainerDark,
  surfaceContainerHigh = surfaceContainerHighDark,
  surfaceContainerHighest = surfaceContainerHighestDark,
)
//endregion
