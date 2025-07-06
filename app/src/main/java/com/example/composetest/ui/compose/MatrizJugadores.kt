package com.example.composetest.ui.compose

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.composetest.R
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.model.iconoDinero
import com.example.composetest.ui.compose.sampledata.cartas
import com.example.composetest.ui.compose.sampledata.jugadores
import com.example.composetest.ui.compose.theme.Hueco
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.theme.ResalteOscuro
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.widget.AdelaidaCard
import com.example.composetest.ui.compose.widget.AdelaidaCardButton
import com.example.composetest.ui.compose.widget.AdelaidaIcon
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.ResalteAnimado
import com.example.composetest.ui.compose.widget.Titulo
import com.example.composetest.ui.compose.modifiers.cuadrado
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel.MostrarElementos
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel.OpcionesClicado

@Composable
fun MatrizJugadores(
    idPartida: Long?,
    jugadores: List<Jugador>?,
    elementosAMostrar: MostrarElementos,
    opcionesClicado: State<OpcionesClicado>,
    configMarcadorDinero: TabJugadoresViewModel.ConfigMarcadorDinero,
    sePuedeClicarEsteElemento: (elementoClicado: ElementoTablero, opcionesQueMostrar: OpcionesClicado) -> Boolean,
    onElementoClicado: (elementoClicado: ElementoTablero, poseedor: Jugador) -> Unit,
    onNombrePulsado: ((jugador: Jugador) -> Unit)?,
    onComprobarNombreRepetido: ((String) -> Boolean)?,
    jugadorConCartasAbiertas: State<Jugador?>,
    onMostrarCartas: (Jugador?) -> Unit,
    onPistaPendienteClicada: (ElementoTablero.Pista, poseedor: Jugador) -> Unit,
    onCancelarAsignacionPistaPendiente: () -> Unit,
    onDevolverPistaPendienteAlTablero: (ElementoTablero.Pista, poseedor: Jugador) -> Unit,
    nivelTitulo: NivelTitulo = NivelTitulo.Nivel2,
) {
    when {
        jugadores == null -> CargandoJugadores()
        jugadores.isEmpty() == true -> SinJugadores()
        else -> Manos(idPartida, jugadores, nivelTitulo, opcionesClicado, configMarcadorDinero,
            sePuedeClicarEsteElemento, onElementoClicado, elementosAMostrar, onNombrePulsado,
            onComprobarNombreRepetido, jugadorConCartasAbiertas, onMostrarCartas,
            onPistaPendienteClicada, onCancelarAsignacionPistaPendiente,
            onDevolverPistaPendienteAlTablero)
    }
}

@Composable
fun CargandoJugadores() {
    Box(Modifier.fillMaxSize(), propagateMinConstraints = true) {
        Cargando()
    }
}

@Composable
fun SinJugadores() {
    Box(Modifier.fillMaxWidth(), propagateMinConstraints = true) {
        AdelaidaText("<Está vacía>", textAlign = TextAlign.Center)
    }
}

@Composable
fun Manos(
    idPartida: Long?,
    jugadores: List<Jugador>,
    nivelTitulo: NivelTitulo = NivelTitulo.Nivel2,
    opcionesClicado: State<OpcionesClicado>,
    configuracionDinero: TabJugadoresViewModel.ConfigMarcadorDinero,
    sePuedeClicarEsteElemento: (elementoClicado: ElementoTablero, opcionesQueMostrar: OpcionesClicado) -> Boolean,
    onCasillaClicada: (elementoClicado: ElementoTablero, poseedor: Jugador) -> Unit,
    elementosAMostrar: MostrarElementos,
    onNombrePulsado: ((jugador: Jugador) -> Unit)?,
    onComprobarNombreRepetido: ((String) -> Boolean)?,
    jugadorConCartasAbierta: State<Jugador?>,
    onMostrarCartas: (Jugador?) -> Unit,
    onPistaPendienteClicada: (ElementoTablero.Pista, poseedor: Jugador) -> Unit,
    onCancelarAsignacionPistaPendiente: () -> Unit,
    onDevolverPistaPendienteAlTablero: (ElementoTablero.Pista, poseedor: Jugador) -> Unit,
) {
    val jugadorConCartasAbiertas by remember { jugadorConCartasAbierta }
    val jugadoreConElementos = jugadores.filtrarPorElementos(elementosAMostrar)

    Box {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = MargenEstandar)
        ) {
            items(jugadoreConElementos, { it.nombre }) { jugador ->
                Mano(
                    jugador,
                    idPartida,
                    elementosAMostrar,
                    nivelTitulo,
                    configuracionDinero,
                    onNombrePulsado,
                    onComprobarNombreRepetido,
                    sePuedeClicarEsteElemento,
                    opcionesClicado,
                    onCasillaClicada,
                    jugador.esElMismoQue(jugadorConCartasAbiertas),
                    jugadorConCartasAbiertas,
                    onMostrarCartas,
                    onPistaPendienteClicada,
                    onCancelarAsignacionPistaPendiente,
                    onDevolverPistaPendienteAlTablero
                )
            }
        }
    }
}

@Composable
private fun Mano(
    jugador: Jugador,
    idPartida: Long?,
    elementosAMostrar: MostrarElementos,
    nivelTitulo: NivelTitulo,
    configuracionDinero: TabJugadoresViewModel.ConfigMarcadorDinero,
    onNombrePulsado: ((Jugador) -> Unit)?,
    onComprobarNombreRepetido: ((String) -> Boolean)?,
    sePuedeClicarEsteElemento: (ElementoTablero, OpcionesClicado) -> Boolean,
    opcionesClicado: State<OpcionesClicado>,
    onCasillaClicada: (ElementoTablero, Jugador) -> Unit,
    mostrarCartas: Boolean,
    jugadorConCartasAbiertas: Jugador?,
    onMostrarCartas: (Jugador?) -> Unit,
    onPistaPendienteClicada: (ElementoTablero.Pista, poseedor: Jugador) -> Unit,
    onCancelarAsignacionPistaPendiente: () -> Unit,
    onDevolverPistaPendienteAlTablero: (ElementoTablero.Pista, poseedor: Jugador) -> Unit,
) {
    /*
     * Si es pantalla horizontal o muy grande:
     * val elementos = jugador.getElementos(elementosAMostrar)
     * ListadoElementos(elementos, sePuedeClicarEsteElemento, opcionesClicado) {
     *    onCasillaClicada(it, jugador)
     * }
     */
    val opcionesClicado by remember { opcionesClicado }

    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column {
            CabeceraJugador(
                jugador, idPartida, nivelTitulo, configuracionDinero, onNombrePulsado,
                onComprobarNombreRepetido, jugadorConCartasAbiertas, onMostrarCartas
            )
            if (elementosAMostrar is MostrarElementos.ManoCompleta) {
                (jugador.getElementos(MostrarElementos.Vitrina(jugador)) as? List<ElementoTablero.Pista>)
                    ?.let { pistas ->
                        Vitrina(jugador, pistas, sePuedeClicarEsteElemento, opcionesClicado,
                            { onCasillaClicada(it, jugador) },
                            onPistaPendienteClicada, onCancelarAsignacionPistaPendiente,
                            { pista ->  onDevolverPistaPendienteAlTablero(pista, jugador) })
                    }
            } else {
                val elementos = jugador.getElementos(elementosAMostrar)
                ListadoElementos(elementos, sePuedeClicarEsteElemento, opcionesClicado, {
                    onCasillaClicada(it, jugador) }, Modifier.padding(horizontal = MargenEstandar))
            }
        }

        if (elementosAMostrar is MostrarElementos.ManoCompleta) {
            val cartas = jugador.getElementos(MostrarElementos.Mano(jugador))
            Cartas(cartas, sePuedeClicarEsteElemento, opcionesClicado, {
                onCasillaClicada(it, jugador)
            }, mostrarCartas, { onMostrarCartas(null) })
        }
    }
}

@Composable
private fun CabeceraJugador(
    jugador: Jugador,
    idPartida: Long?,
    nivelTitulo: NivelTitulo,
    configuracionDinero: TabJugadoresViewModel.ConfigMarcadorDinero,
    onNombrePulsado: ((jugador: Jugador) -> Unit)?,
    onComprobarNombreRepetido: ((String) -> Boolean)?,
    jugadorConCartasAbiertas: Jugador?,
    onMostrarCartas: (Jugador?) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = MargenEstandar, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NombreJugadorEditable(jugador, idPartida, onNombrePulsado, onComprobarNombreRepetido,
            Modifier.weight(1f, false).padding(end = 10.dp), nivelTitulo)
        Row(verticalAlignment = Alignment.CenterVertically) {
            CantidadDinero(jugador, configuracionDinero)
            AbrirCartas(onMostrarCartas, jugador, jugadorConCartasAbiertas)
        }
    }
}

@Composable
private fun AbrirCartas(
    onMostrarCartas: (Jugador?) -> Unit,
    jugador: Jugador,
    jugadorConCartasAbiertas: Jugador?
) {
    AdelaidaCardButton(
        jugador.esElMismoQue(jugadorConCartasAbiertas),
        Modifier.padding(end = 8.dp),
        true,
        { onMostrarCartas(jugador.takeIf { it.noEsElMismoQue(jugadorConCartasAbiertas) }) },
    ) {
        AdelaidaIcon(Icons.Default.AutoAwesomeMotion, "Ver cartas", Modifier.padding(10.dp), tint = Tema.colors.contenidoBoton)
    }
}

@Composable
private fun Vitrina(
    jugador: Jugador,
    pistas: List<ElementoTablero.Pista>,
    sePuedeClicarEsteElemento: (ElementoTablero, OpcionesClicado) -> Boolean,
    opcionesClicado: OpcionesClicado,
    onCasillaClicada: (ElementoTablero) -> Unit,
    onPistaPendienteClicada: (ElementoTablero.Pista, poseedor: Jugador) -> Unit,
    onCancelarAsignacionPistaPendiente: () -> Unit,
    onDevolverPistaPendienteAlTablero: (ElementoTablero.Pista) -> Unit,
) {
    val pistaPendientePulsada = opcionesClicado is OpcionesClicado.PistaPendiente
            && jugador.esElMismoQue(opcionesClicado.jugador)

    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(start = 8.dp)) {
            PistaPendienteDeUbicacion(jugador, pistaPendientePulsada, onPistaPendienteClicada)
            DevolverAlTablero(jugador, pistaPendientePulsada, onDevolverPistaPendienteAlTablero)
        }
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PistasEnLaVitrina(pistas, sePuedeClicarEsteElemento, opcionesClicado, onCasillaClicada,
                pistaPendientePulsada)
        }
    }
}

@Composable
private fun PistaPendienteDeUbicacion(
    poseedor: Jugador,
    estaPulsada: Boolean,
    onPistaPendienteClicada: (ElementoTablero.Pista, poseedor: Jugador) -> Unit
) {
    poseedor.pistaSinUbicar()?.let { pista ->
        AdelaidaCardButton(estaPulsada, onClick = { onPistaPendienteClicada(pista, poseedor) }) {
            AnimatedContent(estaPulsada, contentAlignment = Alignment.Center) {
                if (it) {
                    Box {
                        ElementoMano(pista, true, ElementoMano.Dimensiones.Reducido, invisible = true)
                        AdelaidaIcon(Icons.Default.Close, "Dejar de seleccionar la carta cerrada", Modifier.align(
                            Alignment.Center))
                    }
                } else {
                    ElementoMano(pista, true, ElementoMano.Dimensiones.Reducido)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.DevolverAlTablero(
    jugador: Jugador,
    mostrar: Boolean,
    onDevolverPistaPendienteAlTablero: (ElementoTablero.Pista) -> Unit
) {
    jugador.pistaSinUbicar()?.let {
        AnimatedVisibility(mostrar) {
            AdelaidaCardButton(false, Modifier.padding(top = 10.dp),
                true, { onDevolverPistaPendienteAlTablero(it) }) {
                AdelaidaIcon(
                    painterResource(R.drawable.ic_tablero),
                    "Devolverla al tablero",
                    Modifier
                        .size(48.dp)
                        .padding(10.dp),
                    tint = Tema.colors.contenidoBoton
                )
            }
        }
    }
}

@Composable
private fun PistasEnLaVitrina(
    pistas: List<ElementoTablero.Pista>,
    sePuedeClicarEsteElemento: (ElementoTablero, OpcionesClicado) -> Boolean,
    opcionesClicado: OpcionesClicado,
    onCasillaClicada: (ElementoTablero) -> Unit,
    resaltarParaReemplazo: Boolean
) {
    LazyRow {
        items(3, { it }) {
            ElementoVitrina(pistas.getOrNull(it), sePuedeClicarEsteElemento, opcionesClicado,
                onCasillaClicada, resaltarParaReemplazo)
        }
    }
}

@Composable
private fun ElementoVitrina(
    pista: ElementoTablero?,
    sePuedeClicarEsteElemento: (ElementoTablero, OpcionesClicado) -> Boolean,
    opcionesClicado: OpcionesClicado,
    onCasillaClicada: (ElementoTablero) -> Unit,
    resaltarParaReemplazo: Boolean,
) {
    pista?.let {
        ContenedorElementoMano(it, sePuedeClicarEsteElemento, opcionesClicado, onCasillaClicada,
            resaltarParaReemplazo)
    } ?: run {
        Box(contentAlignment = Alignment.Center) {
            ElementoMano(ElementoTablero.Pista.Objeto(100, 0), invisible = true)
            ElementoVacio()
        }
    }
}

@Composable
private fun ElementoVacio(
    dimensiones: ElementoVacio.Dimensiones = ElementoVacio.Dimensiones.Normales,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val modificadoresReducidos1 = Modifier
        .wrapContentSize()
        .cuadrado()
    val modificadoresNormales = Modifier
        .height(dimensiones.alto.dp)
        .width(dimensiones.ancho.dp)
    val esReducido = dimensiones == ElementoVacio.Dimensiones.Reducido

    val modificadoresDimensiones = modificadoresReducidos1.takeIf { esReducido } ?: modificadoresNormales

    val modifiers = Modifier
        .padding(10.dp)
        .then(modificadoresDimensiones)
        .background(Hueco)
        .shadow(2.dp)


    Box(
        modifiers,
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun Cartas(
    elementos: List<ElementoTablero>,
    sePuedeClicarEsteElemento: (ElementoTablero, OpcionesClicado) -> Boolean,
    opcionesClicado: OpcionesClicado,
    onCasillaClicada: (ElementoTablero) -> Unit,
    mostrarCartas: Boolean,
    cerrarCartas: () -> Unit
) {
    AnimatedVisibility(
        mostrarCartas,
//        enter = slideIn(initialOffset = { fullSize ->
//            IntOffset((fullSize.width / 2), (-fullSize.height / 2))
//        }),
        exit = fadeOut()
    ) {
        Box(Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            var ancho by remember { mutableIntStateOf(10) }
            val anchoFinal = with (LocalDensity.current) { ancho.toDp() }

            AdelaidaCard(Modifier.onGloballyPositioned {
                if (ancho != it.size.width) {
                    ancho = it.size.width
                }
            }) {
                CabeceraCartas(anchoFinal, cerrarCartas)
                ListadoElementos(elementos, sePuedeClicarEsteElemento, opcionesClicado, onCasillaClicada,
                    dimensiones = ElementoMano.Dimensiones.Personalizada(1.15f))
            }
        }
    }
}

@Composable
private fun ColumnScope.CabeceraCartas(anchoFinal: Dp, cerrarCartas: () -> Unit) {
    Row(
        Modifier
            .width(anchoFinal)
            .padding(top = 12.dp, start = MargenEstandar),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Titulo("Cartas", Modifier.weight(1f), nivel = NivelTitulo.Nivel3)
        Equis(cerrarCartas, "Cerrar cartas")
    }
}

@Composable
private fun ListadoElementos(
    elementos: List<ElementoTablero>,
    sePuedeClicarEsteElemento: (ElementoTablero, OpcionesClicado) -> Boolean,
    opcionesClicado: OpcionesClicado,
    onCasillaClicada: (ElementoTablero) -> Unit,
    modifier: Modifier = Modifier,
    dimensiones: ElementoMano.Dimensiones = ElementoMano.Dimensiones.Grande
) {
    LazyRow(contentPadding = PaddingValues(horizontal = MargenEstandar), modifier = modifier) {
        items(elementos, { it.id }) { elemento ->
            ContenedorElementoMano(
                elemento, sePuedeClicarEsteElemento, opcionesClicado,
                onCasillaClicada, false, Modifier.animateItem(), dimensiones
            )
        }
    }
}

@Composable
private fun ContenedorElementoMano(
    elemento: ElementoTablero,
    sePuedeClicarEsteElemento: (ElementoTablero, OpcionesClicado) -> Boolean,
    opcionesClicado: OpcionesClicado,
    onCasillaClicada: (ElementoTablero) -> Unit,
    resaltarParaReemplazo: Boolean,
    modifier: Modifier = Modifier,
    dimensiones: ElementoMano.Dimensiones = ElementoMano.Dimensiones.Grande
) {
    val sePuedeClicar = sePuedeClicarEsteElemento(elemento, opcionesClicado)
    val clic = Modifier.takeIf { !sePuedeClicar }
        ?: Modifier.clickable { onCasillaClicada(elemento) }

//    Box(modifier.then(clic)) {
//        Resalte(resaltarParaReemplazo)
//        ElementoMano(elemento, dimensiones = dimensiones)
//    }

    ResalteAnimado(Tema.colors.resalte  , resaltarParaReemplazo, modifier.then(clic), DpSize(85.dp, 85.dp)) {
        ElementoMano(elemento, dimensiones = dimensiones)
    }
}

@Composable
fun BoxScope.Resalte(resaltarParaReemplazo: Boolean) {
    AnimatedVisibility(resaltarParaReemplazo, Modifier.align(Alignment.Center)) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(85.dp)
                    .background(Color.Transparent)
                    .border(4.dp, ResalteOscuro)
            )

            Box(
                Modifier
                    .size(65.dp)
                    .background(Color.Transparent)
                    .border(2.dp, ResalteOscuro)
            )
        }
    }
}

private fun List<Jugador>.filtrarPorElementos(mostrarElementos: MostrarElementos): Array<Jugador> =
    when (mostrarElementos) {
        is MostrarElementos.ManoCompleta -> this
        is MostrarElementos.PistasRasgos -> filter {
            it noEsElMismoQue mostrarElementos.comprador && it.manoComprable().any(mostrarElementos.predicado)
        }
        is MostrarElementos.Secretos -> filter {
            it noEsElMismoQue mostrarElementos.comprador && it.manoComprable().any(mostrarElementos.predicado)
        }
        is MostrarElementos.Gastadas -> filter { it.gastadas().isNotEmpty() }
        is MostrarElementos.Vitrina -> filter { it noEsElMismoQue mostrarElementos.ladron && it.pistas().isNotEmpty() }
        is MostrarElementos.Mano -> filter { it noEsElMismoQue mostrarElementos.ladron && it.cartas().isNotEmpty() }
    }.toTypedArray()

private fun Jugador.getElementos(cuales: MostrarElementos): List<ElementoTablero> =
    when (cuales) {
        is MostrarElementos.PistasRasgos -> manoComprable().filter(cuales.predicado)
        is MostrarElementos.Secretos -> manoComprable().filter(cuales.predicado)
        is MostrarElementos.ManoCompleta -> manoSinDinero()
        is MostrarElementos.Gastadas -> gastadas()
        is MostrarElementos.Mano -> cartas().filter(cuales.predicado)
        is MostrarElementos.Vitrina -> pistas()
    }

@Composable
private fun CantidadDinero(jugador: Jugador, configuracionDinero: TabJugadoresViewModel.ConfigMarcadorDinero) {
    if (configuracionDinero.mostrar) {
        jugador.dinero.takeIf { it > 0 }?.toString()?.let {
            Titulo(
                "$it $iconoDinero", modifier = Modifier
                    .clickable { configuracionDinero.onDineroClicado(jugador) }
                    .padding(start = 8.dp, end = 10.dp),
                nivel = NivelTitulo.Nivel2
            )
        }
    }
}

interface ElementoVacio {

    interface Dimensiones {
        val alto: Int
        val ancho: Int

        companion object {
            val Reducido = object: Dimensiones {
                override val ancho: Int = 55 // Ignorada
                override val alto: Int = 55 // Ignorada
            }

            val Normales = object: Dimensiones {
                override val alto: Int = 80
                override val ancho: Int = 65
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview(name = "Vitrina", showBackground = true, backgroundColor = 0xFF5D4B4B)
private fun P1() {
    val jugador = jugadores("Manuel")[0].let {
        it.copy(pistas = (it.pistas() + ElementoTablero.Pista.Testigo(1)).toMutableList())
//        it.copy(pistas = (it.pistas().take(2)).toMutableList())
    }

    Vitrina(
        jugador,
        jugador.pistas(),
        { _, _ -> false },
        OpcionesClicado.PistaPendiente(jugador),
        {},
        { _, _ -> },
        {},
        {}
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview(name = "Cartas")
private fun P2() {
    Cartas(cartas(), { _, _  -> false }, OpcionesClicado.OpcionesCasilla(), {}, true, {})
}

@SuppressLint("UnrememberedMutableState")
@Preview(name = "MatrizJugadores")
@Composable
fun P3() {
    val jugadores = jugadores("Bastian", "Fujur", "Atreyu", "La puta de la emperatriz")

    MatrizJugadores(
        7L,
        null, // NombreJugadorEditable usa un ViewModel, por lo que no se puede mostrar
        MostrarElementos.ManoCompleta(),
        mutableStateOf(OpcionesClicado.OpcionesCasilla()),
        TabJugadoresViewModel.ConfigMarcadorDinero(true, true, {}),
        { _, _ -> true },
        { _, _ -> },
        {},
        null,
        mutableStateOf(jugadores[2]),
        {},
        { _, _ -> },
        {},
        { _, _ -> }
    )
}