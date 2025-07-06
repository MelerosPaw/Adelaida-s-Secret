package com.example.composetest.ui.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.composetest.ui.compose.modifiers.resalte

private val RESALTE_DECOR_SIZE = DpSize(26.dp, 26.dp)

@Composable
fun ResalteAnimado(
    color: Color,
    resaltado: Boolean,
    modifier: Modifier = Modifier,
    size: DpSize? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current

    // En caso de que no se haya especificado un tamaño, hay que saber cuánto mide el contenido
    var calculatedContentSize by remember { mutableStateOf(null as IntSize?) }

    // Cuando tengamos el tamaño del contenido, le aplicamos el tamaño de las decoraciones para
    // asignárselo al contenedor y al resalte
    val contentSize = calculatedContentSize?.let { calculateResalteBoxSize(null, it, density) }

    // Si tenemos un tamaño fijo, aplicaremos directamente ese tamaño al resalte y no nos importa
    // el tamaño del contenido. En teoría, no debe ser mayor que el tamaño del resalte (no se ha
    // contemplado el caso de que lo sea).
    val sizeMod = if (size == null) {
        contentSize?.let { Modifier.size(it) } ?: Modifier
    } else {
        Modifier
    }

    Box(modifier
        .then(sizeMod)
        .onGloballyPositioned {
            // Aquí escuchamos el cambio de las medidas y lo actualizamos solo si el cambio es real
            if (sizeHasChanged(calculatedContentSize, it.size, density)) {
                calculatedContentSize = it.size
            }
        }, contentAlignment = Alignment.Center) {

        // Primero va el contenedor del resalte, que medirá
        AnimatedVisibility(resaltado, Modifier.align(Alignment.Center)) {
            val resalteSize = calculateResalteBoxSize(size, calculatedContentSize, density)
            Box(Modifier.resalte(color, resaltado, resalteSize))
        }

        content.invoke(this@Box)
    }
}

/**
 * Si el tamaño del contenedor cambia, cambiará la primra vez de 0 x 0 a lo que tenga que medir.
 * A esa cantidad se le añadirá el tamaño de las decoraciones del resalte, por lo que volverá a
 * cambiar. Por eso, para saber si se trata de un verdadero cambio o solo de aquel producido por
 * aplicar el resalte, debemos comprobar que el nuevo tamaño no sea igual al tamaño anterior más el
 * de las decoraciones, en cuyo caso, no se tratará de un auténtico cambio.
 *
 * @param currentCalculatedContentSize El tamaño anterior del contenedor. Si es nulo, será la
 * es que aún no se ha medido, por lo que debemos considerarlo cambio. Si no, habrá que comprobar
 * que el cambio sea auténtico.
 */
private fun sizeHasChanged(
    currentCalculatedContentSize: IntSize?,
    newContentSize: IntSize,
    density: Density
): Boolean {
    val resalteDecorWidthInPx = with(density) { RESALTE_DECOR_SIZE.width.toPx().toInt() }
    val resalteDecorHeightInPx = with(density) { RESALTE_DECOR_SIZE.height.toPx().toInt() }
    return currentCalculatedContentSize == null || (currentCalculatedContentSize != newContentSize
            && newContentSize.height > (currentCalculatedContentSize.height + resalteDecorHeightInPx)
            && newContentSize.width > (currentCalculatedContentSize.width + resalteDecorWidthInPx))
}

/**
 * Si se ha especificado un tamaño para el resalte, se usará. Si no, el resalte deberá aplicarse al
 * contenido al completo. Para este segundo caso, se tomará el tamaño del contenido más el tamaño
 * de las decoraciones del resalte (padding + bordes) para que este se vea por fuera del contenido.
 */
@Composable
private fun calculateResalteBoxSize(specifiedSize: DpSize?, contentSize: IntSize?, density: Density): DpSize =
    specifiedSize
        ?: contentSize?.let {
            with(density) {
                DpSize(
                    contentSize.width.toDp() + RESALTE_DECOR_SIZE.width,
                    contentSize.height.toDp() + RESALTE_DECOR_SIZE.height
                )
            }
        } ?: DpSize(0.dp, 0.dp)