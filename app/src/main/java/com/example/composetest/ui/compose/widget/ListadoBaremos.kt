package com.example.composetest.ui.compose.widget

import BotonDialogo
import Dimensiones
import Estilo
import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import com.example.composetest.model.Baremo
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.model.TipoPista
import com.example.composetest.ui.compose.ElementoMano
import com.example.composetest.ui.compose.sampledata.baremos
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.theme.FondoBaremoSeleccionado
import com.example.composetest.ui.compose.theme.FondoBaremoSinSeleccionar
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.TextoBaremoSeleccionado
import com.example.composetest.ui.compose.theme.TextoBaremoSinSeleccionar

private const val CLAVE__SELECCIONADO = "SELECCIONADO"
private const val CLAVE__NO_SELECCIONABLES = "NO_SELECCIONABLES"

@Composable
fun ListadoBaremos(
    state: ListadoBaremosState,
    jugador: Jugador,
    baremos: List<Baremo>,
    guardarBaremo: (jugador: Jugador, baremo: Baremo) -> Unit,
    cancelar: () -> Unit,
) {
    val seleccionado by remember { state.baremoSeleccionado }

    Column {
            Titulo(
                "Selecciona un baremo para ${jugador.nombre}",
                Modifier.fillMaxWidth().padding(bottom = MargenEstandar),
                NivelTitulo.Nivel2, TextAlign.Center
            )

        LazyVerticalGrid(
            GridCells.FixedSize(140.dp),
            Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(baremos, { index, item -> item.id }) { index, item ->
                ElementoBaremo(state, item)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            BotonDialogo("Seleccionar", seleccionado != null, Estilo.Mayusculas(false), Dimensiones.WrapContent) {
                seleccionado?.let { guardarBaremo(jugador, Baremo(it, emptyArray())) }
            }
            BotonDialogo("Cancelar", true, Estilo.Mayusculas(false), Dimensiones.WrapContent, cancelar)
        }
    }
}

@Composable
fun ElementoBaremo(state: ListadoBaremosState, baremo: Baremo) {
    val seleccionado = state.estaSeleccionado(baremo)
    val esSeleccionable = state.esSeleccionable(baremo)

    Column(Modifier
        .border(2.dp, state.getColorBorde(seleccionado), AdelaidaCardDefaults.shape())
        .padding(top = 4.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        AdelaidaText(baremo.id, fontWeight = state.getPesoFuenteId(seleccionado))

        val alpha = Modifier.alpha(state.getAlpha(esSeleccionable))

        AdelaidaCard(Modifier
            .then(alpha)
            .padding(top = 6.dp)
            .border(1.dp, Color.Black, AdelaidaCardDefaults.shape()),
            AdelaidaCardDefaults.colors(state.getColorFondo(seleccionado))
        ) {
            val clicable = Modifier.clickable { state.onBaremoClicado(baremo) }
                .takeIf { esSeleccionable }
                ?: Modifier

            Column(Modifier
                .then(clicable)
                .then(alpha)
                .padding(horizontal = MargenEstandar)) {
                val colorTexto = state.getColorTexto(seleccionado)

                FilaBaremo(state.getElementoHabito(baremo), state.getElementoObjeto(baremo), colorTexto)
                FilaBaremo(state.getElementoTestigo(baremo), state.getElementoCoartada(baremo), colorTexto)
                FilaBaremo(state.getElementoSecreto(baremo), state.getElementoRestoObjetos(), colorTexto)
            }
        }
    }
}

@Composable
private fun FilaBaremo(
    izquierda: ElementoTablero,
    derecha: ElementoTablero,
    colorTexto: Color,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconoBaremo(izquierda, colorTexto)
        IconoBaremo(derecha, colorTexto)
    }
}

@Composable
fun IconoBaremo(elemento: ElementoTablero, colorTexto: Color) {
    ElementoMano(
        elemento,
        dimensiones = ElementoMano.Dimensiones.Reducido,
        colorTexto = colorTexto
    )
}

@Composable
fun rememberListadoBaremosState(
    baremoSeleccionado: Baremo?,
    baremosNoSeleccionables: List<Baremo>
): ListadoBaremosState = rememberSaveable(
    saver = Saver<ListadoBaremosState, Bundle>(
    save = {
        bundleOf(
            CLAVE__SELECCIONADO to it.baremoSeleccionado.value,
            CLAVE__NO_SELECCIONABLES to ArrayList(baremosNoSeleccionables.map { it.id })
        )
    },
    restore = {
        ListadoBaremosState(
            it.getString(CLAVE__SELECCIONADO),
            it.getStringArrayList(CLAVE__NO_SELECCIONABLES).orEmpty()
        )
    })
) {
    ListadoBaremosState(baremoSeleccionado?.id, baremosNoSeleccionables.map { it.id })
}

@Stable
class ListadoBaremosState(baremoSeleccionado: String?, private val baremosNoSeleccionables: List<String>) {

    private val _baremoSeleccionado: MutableState<String?> = mutableStateOf(baremoSeleccionado)
    val baremoSeleccionado: State<String?> = _baremoSeleccionado

    //region Public methods
    fun getColorBorde(seleccionado: Boolean) =
        Color(0xFFBC9913).takeIf { seleccionado } ?: Color.Transparent

    fun getColorFondo(seleccionado: Boolean): Color =
        FondoBaremoSeleccionado.takeIf { seleccionado } ?: FondoBaremoSinSeleccionar

    fun getColorTexto(seleccionado: Boolean): Color =
        TextoBaremoSeleccionado.takeIf { seleccionado } ?: TextoBaremoSinSeleccionar

    fun getAlpha(esSeleccionable: Boolean): Float = 1f.takeIf { esSeleccionable } ?: 0.35f

    fun getPesoFuenteId(seleccionado: Boolean): FontWeight =
        FontWeight.Black.takeIf { seleccionado } ?: AdelaidaTextDefaults.fontWeight

    fun getElementoHabito(baremo: Baremo): ElementoTablero =
        ElementoTablero.Pista.Habito(baremo.valores[TipoPista.Habito().posicionEnBaremo])

    fun getElementoObjeto(baremo: Baremo): ElementoTablero =
        ElementoTablero.Pista.Objeto(baremo.valores[TipoPista.Objeto().posicionEnBaremo])

    fun getElementoTestigo(baremo: Baremo): ElementoTablero =
        ElementoTablero.Pista.Testigo(baremo.valores[TipoPista.Testigo().posicionEnBaremo])

    fun getElementoCoartada(baremo: Baremo): ElementoTablero =
        ElementoTablero.Pista.Coartada(baremo.valores[TipoPista.Coartada().posicionEnBaremo])

    fun getElementoSecreto(baremo: Baremo): ElementoTablero =
        ElementoTablero.Pista.Secreto(baremo.valores[TipoPista.Secreto().posicionEnBaremo])

    fun getElementoRestoObjetos(): ElementoTablero = ElementoTablero.Carta.Dinero(0, 1)

    fun estaSeleccionado(baremo: Baremo) = baremo.id == baremoSeleccionado.value

    fun esSeleccionable(baremo: Baremo): Boolean =  baremo.id !in baremosNoSeleccionables

    fun onBaremoClicado(baremo: Baremo) {
        _baremoSeleccionado.value = baremo.id
    }
}

@Composable
@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun PreviewElementoBaremo() {
    val baremos = baremos(1)
    val state = rememberListadoBaremosState(baremos.first(), emptyList())
    ElementoBaremo(state, baremos.first())
}

@Composable
@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun PreviewListadoBaremos() {
    val baremos = baremos(36)
    val state = rememberListadoBaremosState(baremos[3], listOf(baremos[1]))
    ListadoBaremos(state, jugadores("Alguien").first(), baremos, { _, _ -> }, {})
}