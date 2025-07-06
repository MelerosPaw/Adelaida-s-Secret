package com.example.composetest.ui.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
private fun BaseTheme(
  useDarkTheme: Boolean,
  // Dynamic color is available on Android 12+
  useDynamicColors: Boolean = false,
  extendedColors: TokensColores,
  content: @Composable () -> Unit
) {
  CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
    MaterialTheme(
      colorScheme = adelaidaColorScheme(useDynamicColors, useDarkTheme),
      typography = adelaidaTypography(),
      content = content
    )
  }
}

@Composable
fun AdelaidaTheme(content: @Composable () -> Unit) {
  val isDarkTheme = isSystemInDarkTheme()
  val extendedColors = TokensColores(
    fondoPantalla = claroOscuro(isDarkTheme, FondoPantallaClaro, FondoPantallaOscuro),
    statusBar = BarraEstado,
    toolbarContainer = claroOscuro(isDarkTheme, ToolbarClaro, ToolbarOscuro),
    toolbarContent = Color.White,
    tituloInicioRonda = claroOscuro(isDarkTheme, TextoClaro, TextoOscuro),
    subtituloInicioRonda = claroOscuro(isDarkTheme, TextoInhabilitadoClaro, TextoInhabilitadoOscuro),
    texto = claroOscuro(isDarkTheme, TextoClaro, TextoOscuro),
    rellenoBoton = claroOscuro(isDarkTheme, RellenoBotonClaro, RellenoBotonOscuro),
    rellenoBotonPulsado = claroOscuro(isDarkTheme, RellenoBotonPulsadoClaro, RellenoBotonPulsadoOscuro),
    contenidoBoton = claroOscuro(isDarkTheme, TextoBotonClaro, TextoBotonOscuro),
    textoBotonInhabilitado = claroOscuro(isDarkTheme, TextoBotonInhabilitadoClaro, TextoBotonInhabilitadoOscuro),
    decoradoBoton = claroOscuro(isDarkTheme, DecoradoBotonClaro, DecoradoBotonOscuro),
    resalte = claroOscuro(isDarkTheme, ResalteClaro, ResalteOscuro),
    fondoListas = claroOscuro(isDarkTheme, FondoListasClaro, FondoListasOscuro),
    iconos = claroOscuro(isDarkTheme, IconosClaro, IconosOscuro),
    fondoCampoTexto = claroOscuro(isDarkTheme, FondoCampoTextoClaro, FondoCampoTextoOscuro),
    fondoCampoTextoSeleccionado = claroOscuro(isDarkTheme, FondoCampoTextoSeleccionadoClaro, FondoCampoTextoSeleccionadoOscuro),
    errorCampoTexto = claroOscuro(isDarkTheme, ErrorCampoTextoClaro, ErrorCampoTextoOscuro),
    indicadorSeleccionBarraNavegacion = claroOscuro(isDarkTheme, IndicadorSeleccionBarraNavegacionClaro, IndicadorSeleccionadoBarraNavegacionOscuro),
    iconoBarraNavegacion = Color.White,
    iconoBarraNavegacionSelecionado = claroOscuro(isDarkTheme, ToolbarClaro, Color.White),
    fondoDialogos = claroOscuro(isDarkTheme, RellenoDialogoClaro, RellenoDialogoOscuro),
    contenidoDialogos = claroOscuro(isDarkTheme, ContenidoDialogoClaro, ContenidoDialogoOscuro),
    contenidoDialogosInhabilitado = claroOscuro(isDarkTheme, ContenidoInhabilitadoDialogoClaro, ContenidoInhabilitadoDialogoOscuro),
    textoCuadroTexto = claroOscuro(isDarkTheme, TextoCampoTextoClaro, TextoCampoTextoOscuro),
    fondoCheckboxMarcado = claroOscuro(isDarkTheme, CheckboxMarcadoClaro, CheckboxMarcadoOscuro),
    fondoCheckboxDesmarcado = claroOscuro(isDarkTheme, CheckboxDesmarcadoClaro, CheckboxDesmarcadoOscuro),
    fondoCheckboxInhabilitado = claroOscuro(isDarkTheme, TextoBotonInhabilitadoClaro, TextoBotonInhabilitadoOscuro),
    marcaCheckbox = claroOscuro(isDarkTheme, CheckboxMarcaClaro, CheckboxMarcaOscuro),
    divisor = claroOscuro(isDarkTheme, DivisorHorizontalClaro, DivisorHorizontalOscuro),
  )

  BaseTheme(useDarkTheme = isDarkTheme, extendedColors = extendedColors, content = content)
}

@Composable
fun AdelaidaThemeVerde(content: @Composable () -> Unit) {
  val isDarkTheme = isSystemInDarkTheme()
  val extendedColors = TokensColores(
    fondoPantalla = claroOscuro(isDarkTheme, FondoPantallaVerdeClaro, FondoPantallaVerdeOscuro),
    statusBar = BarraEstadoVerde,
    toolbarContainer = claroOscuro(isDarkTheme, ToolbarVerdeClaro, ToolbarVerdeOscuro),
    toolbarContent = Color.White,
    texto = claroOscuro(isDarkTheme, TextoVerdeClaro, TextoVerdeOscuro),
    tituloInicioRonda = claroOscuro(isDarkTheme, TextoClaro, TextoOscuro),
    subtituloInicioRonda = claroOscuro(isDarkTheme, TextoInhabilitadoVerdeClaro, TextoInhabilitadoVerdeOscuro),
    rellenoBoton = claroOscuro(isDarkTheme, RellenoBotonVerdeClaro, RellenoBotonVerdeOscuro),
    rellenoBotonPulsado = claroOscuro(isDarkTheme, RellenoBotonPulsadoVerdeClaro, RellenoBotonPulsadoVerdeOscuro),
    contenidoBoton = claroOscuro(isDarkTheme, TextoBotonVerdeClaro, TextoBotonVerdeOscuro),
    textoBotonInhabilitado = claroOscuro(isDarkTheme, TextoBotonInhabilitadoVerdeClaro, TextoBotonInhabilitadoVerdeOscuro),
    decoradoBoton = claroOscuro(isDarkTheme, DecoradoBotonVerdeClaro, DecoradoBotonOscuro),
    resalte = claroOscuro(isDarkTheme, ResalteVerdeClaro, ResalteVerdeOscuro),
    fondoListas = claroOscuro(isDarkTheme, FondoListasVerdeClaro, FondoListasVerdeOscuro),
    iconos = claroOscuro(isDarkTheme, IconosVerdeClaro, IconosVerdeOscuro),
    fondoCampoTexto = claroOscuro(isDarkTheme, FondoCampoTextoVerdeClaro, FondoCampoTextoVerdeOscuro),
    fondoCampoTextoSeleccionado = claroOscuro(isDarkTheme, FondoCampoTextoSeleccionadoVerdeClaro, FondoCampoTextoSeleccionadoVerdeOscuro),
    errorCampoTexto = claroOscuro(isDarkTheme, ErrorCampoTextoVerdeClaro, ErrorCampoTextoVerdeOscuro),
    indicadorSeleccionBarraNavegacion = claroOscuro(isDarkTheme, IndicadorSeleccionadoBarraNavegacionVerdeClaro, IndicadorSeleccionadoBarraNavegacionVerdeOscuro),
    iconoBarraNavegacion = Color.White,
    iconoBarraNavegacionSelecionado = claroOscuro(isDarkTheme, ToolbarVerdeClaro, Color.White),
    fondoDialogos = claroOscuro(isDarkTheme, RellenoDialogoClaro, RellenoDialogoOscuro),
    contenidoDialogos = claroOscuro(isDarkTheme, ContenidoDialogoClaro, ContenidoDialogoOscuro),
    contenidoDialogosInhabilitado = claroOscuro(isDarkTheme, ContenidoInhabilitadoDialogoClaro, ContenidoInhabilitadoDialogoOscuro),
    textoCuadroTexto = claroOscuro(isDarkTheme, TextoCampoTextoVerdeClaro, TextoCampoTextoVerdeOscuro),
    fondoCheckboxMarcado = claroOscuro(isDarkTheme, CheckboxMarcadoVerdeClaro, CheckboxMarcadoVerdeOscuro),
    fondoCheckboxDesmarcado = claroOscuro(isDarkTheme, CheckboxDesmarcadoVerdeClaro, CheckboxDesmarcadoVerdeOscuro),
    fondoCheckboxInhabilitado = claroOscuro(isDarkTheme, TextoBotonInhabilitadoVerdeClaro, TextoBotonInhabilitadoVerdeOscuro),
    marcaCheckbox = claroOscuro(isDarkTheme, CheckboxMarcaVerdeClaro, CheckboxMarcaVerdeOscuro),
    divisor = claroOscuro(isDarkTheme, DivisorHorizontalClaro, DivisorHorizontalOscuro),
  )
  BaseTheme(useDarkTheme = isDarkTheme, extendedColors = extendedColors, content = content)
}

@Composable
private fun claroOscuro(isDarkTheme: Boolean, claro: Color, oscuro: Color): Color =
  oscuro.takeIf { isDarkTheme } ?: claro

fun canUseDynamicColor(useDynamicTheme: Boolean): Boolean =
  useDynamicTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

object Tema {

  val colors: TokensColores
    @Composable
    get() = LocalExtendedColors.current
}