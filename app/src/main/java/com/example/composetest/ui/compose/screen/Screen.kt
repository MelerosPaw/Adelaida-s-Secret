package com.example.composetest.ui.compose.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import com.example.composetest.ui.compose.navegacion.SetUpStatusBar
import com.example.composetest.ui.compose.theme.AdelaidaTheme
import com.example.composetest.ui.compose.theme.AdelaidaThemeVerde
import com.example.composetest.ui.compose.theme.Tema

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun isLandscape(): Boolean = (LocalContext.current as? Activity)?.let {
  calculateWindowSizeClass(it).widthSizeClass == WindowWidthSizeClass.Expanded
} == true

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    configuracionToolbar: NavegadorCreacion.ConfiguracionToolbar? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
  AdelaidaTheme {
    Contenido(modifier, configuracionToolbar, content)
  }
}

@Composable
fun ScreenVerde(
  modifier: Modifier = Modifier,
  configuracionToolbar: NavegadorCreacion.ConfiguracionToolbar? = null,
  content: @Composable BoxScope.() -> Unit = {}
) {
  AdelaidaThemeVerde {
      Contenido(modifier, configuracionToolbar, content)
  }
}

@Composable
fun PreviewFondo(content: @Composable BoxScope.() -> Unit = {}) {
  AdelaidaTheme {
    Surface {
      Box(Modifier.background(Tema.colors.fondoPantalla), content = content)
    }
  }
}

@Composable
fun PreviewFondoVerde(content: @Composable BoxScope.() -> Unit = {}) {
  AdelaidaThemeVerde {
    Surface {
      Box(Modifier.background(Tema.colors.fondoPantalla), content = content)
    }
  }
}

@Composable
fun ScreenPreviewMarron(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
  AdelaidaTheme {
    Surface(modifier = modifier) {
      Box(Modifier
        .fillMaxSize()
        .background(Tema.colors.fondoPantalla), content = content)
    }
  }
}

@Composable
fun PreviewComponente(content: @Composable BoxScope.() -> Unit) {
  Column {
    Text("Marrón", Modifier.padding(bottom = 10.dp), color = Color.White)
    PreviewFondo(content)

    Spacer(Modifier.height(10.dp))

    Text("Verde", Modifier.padding(bottom = 10.dp), color = Color.White)
    PreviewFondoVerde(content)
  }
}

@Composable
fun ScreenPreviewVerde(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
  AdelaidaThemeVerde {
    Surface(modifier = modifier) {
      Box(Modifier
        .fillMaxSize()
        .background(Tema.colors.fondoPantalla), content = content)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Contenido(
  modifier: Modifier,
  configuracionToolbar: NavegadorCreacion.ConfiguracionToolbar?,
  content: @Composable BoxScope.() -> Unit
) {
    val topAppBar: @Composable () -> Unit = configuracionToolbar?.let {
        {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = configuracionToolbar.getToolbarContainerColor()),
                title = configuracionToolbar.titulo,
                navigationIcon = configuracionToolbar.iconoNavegacion,
                actions = configuracionToolbar.actions,
            )
        }
    } ?: { }

  // TODO Melero: 13/4/25 Aquí hay conflicto. El color de la estatus bar ¿cuál es? ¿El que está
  //  dentro de esta función o el del fondo del scaffold, porque se le está aplicacndo el cutout padding?
  SetUpStatusBar(configuracionToolbar?.getStatusBarColor() ?: Tema.colors.fondoPantalla)

  Surface(modifier = modifier, color = Tema.colors.statusBar) { // Color que se verá en la parte del cutout
    Scaffold(
      topBar = topAppBar,
      modifier = Modifier.displayCutoutPadding().fillMaxSize()
    ) { innerPadding ->
      Box(Modifier
        .fillMaxSize()
        .background(Tema.colors.fondoPantalla)
        .padding(innerPadding), content = content)
    }
  }
}