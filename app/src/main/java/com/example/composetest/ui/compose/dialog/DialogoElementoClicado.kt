package com.example.composetest.ui.compose.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.ui.compose.AccionProhibida
import com.example.composetest.ui.compose.HtmlSpan
import com.example.composetest.ui.compose.widget.dialog.AdelaidaButtonDialog
import com.example.composetest.ui.compose.widget.dialog.OpcionDialogo
import com.example.composetest.ui.viewmodel.DialogoElementoClicadoViewModel

@Composable
fun DialogoElementoClicado(
    elemento: ElementoTablero,
    listaJugadores: List<Jugador>,
    owner: Jugador?,
    conjuntoOpciones: ConjuntoOpciones,
    idPartida: Long,
    onAccionProhibida: (AccionProhibida) -> Unit,
    onDismiss: (Resultado) -> Unit
) {
    val viewModel: DialogoElementoClicadoViewModel = hiltViewModel()
    viewModel.onAccionProhibida = onAccionProhibida

    CuadroDialogoElementoClicado(elemento, conjuntoOpciones, owner, viewModel, onDismiss)
    CuadroDialogoAsignarElemento(elemento, listaJugadores, owner, idPartida, viewModel, onDismiss)
    CuadroDialogoLeerPista(viewModel, onDismiss)
}

@Composable
private fun CuadroDialogoElementoClicado(
    elemento: ElementoTablero,
    conjuntoOpciones: ConjuntoOpciones,
    owner: Jugador?,
    viewModel: DialogoElementoClicadoViewModel,
    onDismiss: (Resultado) -> Unit
) {
    val tipo = "pista".takeIf { elemento is ElementoTablero.Pista } ?: "carta"
    val opciones = when (conjuntoOpciones) {
        ConjuntoOpciones.TABLERO -> Opciones.Tablero(elemento, viewModel, onDismiss)
        ConjuntoOpciones.JUGADOR -> owner?.let { Opciones.Jugador(elemento, it, viewModel, onDismiss) }
    }

    opciones?.obtener()?.let {
        AdelaidaButtonDialog<String?>("¿Qué quieres hacer con esta $tipo?", it, elemento)
    }
}

@Composable
fun CuadroDialogoAsignarElemento(
    elemento: ElementoTablero,
    listaJugadores: List<Jugador>,
    owner: Jugador?,
    idPartida: Long,
    viewModel: DialogoElementoClicadoViewModel,
    onDismiss: (Resultado) -> Unit,
) {
    val mostrarDialogo by remember { viewModel.dialogoSeleccionJugadores }

    if (mostrarDialogo) {
        DialogoAsignarElemento(elemento, listaJugadores, owner, idPartida, true) { resultado ->
            viewModel.ocultarDialogoSeleccionJugadores()
            onDismiss(resultado)
        }
    }
}

@Composable
fun CuadroDialogoLeerPista(viewModel: DialogoElementoClicadoViewModel, onDismiss: (Resultado) -> Unit) {
    val pistaParaLeer by remember { viewModel.dialogoLeerPista }

    pistaParaLeer?.let { pista ->
        val html = HtmlSpan(pista.texto)
        val onDismiss = { onDismiss(Resultado.Leer(pista.pista)) }
        AdelaidaButtonDialog(
            html.text,
            arrayOf(OpcionDialogo("Cerrar", null) {
                viewModel.ocultarDialogoLeerPista()
                onDismiss
            }),
            pista.pista,
            html.spans,
            fontWeight = FontWeight.Normal,
            fullWidth = true,
        ) {
            viewModel.ocultarDialogoLeerPista()
            onDismiss()
        }
    }
}

enum class ConjuntoOpciones {
    TABLERO, JUGADOR
}

private sealed class Opciones(
    val elemento: ElementoTablero,
    val viewModel: DialogoElementoClicadoViewModel,
    val onDismiss: (Resultado) -> Unit,
) {

    abstract fun obtener(): Array<OpcionDialogo<String?>>

    class Tablero(
        elemento: ElementoTablero,
        viewModel: DialogoElementoClicadoViewModel,
        onDismiss: (Resultado) -> Unit
    ) : Opciones(elemento, viewModel, onDismiss) {

        override fun obtener(): Array<OpcionDialogo<String?>> {
            val asignar = OpcionDialogo<String?>("Asignar a...", null) { viewModel.abrirDialogoSeleccionJugadores(elemento) }
            val nada = OpcionDialogo<String?>("Nada", null) { onDismiss(Resultado.Dismiss) }

            val segundaOpcion = when (elemento) {
                is ElementoTablero.Pista -> OpcionDialogo<String?>("Leer", null) {
                    viewModel.leerPista(elemento)
                }
                is ElementoTablero.Carta -> nada
            }

            val terceraOpcion = when (elemento) {
                is ElementoTablero.Pista -> nada
                is ElementoTablero.Carta -> null
            }

            return listOfNotNull(asignar, segundaOpcion, terceraOpcion).toTypedArray()
        }
    }

    class Jugador(
      elemento: ElementoTablero,
      val owner: com.example.composetest.model.Jugador,
      viewModel: DialogoElementoClicadoViewModel,
      onDismiss: (Resultado) -> Unit,
    ) : Opciones(elemento, viewModel, onDismiss) {

        override fun obtener(): Array<OpcionDialogo<String?>> {
            val asignar = OpcionDialogo<String?>("Asignar a...", null) {
                viewModel.abrirDialogoSeleccionJugadores(elemento)
            }
            val leerOUsar = when (elemento) {
                is ElementoTablero.Pista -> OpcionDialogo<String?>("Leer", null) {
                    viewModel.leerPista(elemento)
                }
                is ElementoTablero.Carta -> OpcionDialogo<String?>("Usar", null) {
                    onDismiss(Resultado.Usar(elemento, owner))
                }
            }
            val nada = OpcionDialogo<String?>("Nada", null) { onDismiss(Resultado.Dismiss) }

            return listOfNotNull(asignar, leerOUsar, nada).toTypedArray()
        }
    }
}

sealed class Resultado {
    class Leer(val pista: ElementoTablero.Pista): Resultado()
    class Usar(val cartaUsada: ElementoTablero.Carta, val owner: Jugador) : Resultado()
    /**
     * @param nombreJugadorAsignado El nombre del jugador que se queda la carta. Si es null, es una
     * devolución al tablero.
     */
    class Asignado(val elementoAsignado: ElementoTablero, val nombreJugadorAsignado: String?) :
        Resultado()
    class Error() : Resultado()
    object Dismiss : Resultado()
}