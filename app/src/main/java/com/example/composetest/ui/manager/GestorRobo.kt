package com.example.composetest.ui.manager

import com.example.composetest.model.ElementoTablero
import com.example.composetest.model.Jugador
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel.MostrarElementos

class GestorRobo(
    val idPartida: Long,
    val jugadores: List<Jugador>,
    val ladron: Jugador,
    val brandy: ElementoTablero.Carta.Brandy,
    val mostrarDialogoBrandy: (AccionRobo) -> Unit,
    val ocultarDialogoBrandy: () -> Unit,
    val ejecutarAsuntoTurbio: (AsuntoTurbio) -> Unit
) {

    fun robar() {
        mostrarDialogoBrandy(getOpcionRobo(ladron))
    }

    fun robarVitrina() {
        crearElementosParaRobar(MostrarElementos.Vitrina(ladron))
    }

    fun robarMano() {
        crearElementosParaRobar(MostrarElementos.Mano(ladron))
    }

    private fun getOpcionRobo(ladron: Jugador): AccionRobo {
        val hayPistas = hayPistas(ladron)
        val hayCartas = hayCartas(ladron)

        val opcionCompra = when {
            !hayPistas && !hayCartas -> OpcionRobo.NadaDisponible()
            else -> OpcionRobo.Robar(hayPistas, hayCartas)
        }

        return AccionRobo(ladron, opcionCompra)
    }

    private fun hayPistas(ladron: Jugador): Boolean = jugadores.any {
        it != ladron && it.pistas().isNotEmpty()
    }

    private fun hayCartas(ladron: Jugador): Boolean = jugadores.any {
        it != ladron && it.cartas().isNotEmpty()
    }

    private fun crearElementosParaRobar(mostrarElementos: MostrarElementos) {
        ejecutarAsuntoTurbio(AsuntoTurbio.Robo(mostrarElementos))
        ocultarDialogoBrandy()
    }

    class AccionRobo(
        val ladron: Jugador,
        val opcion: OpcionRobo
    )

    sealed class OpcionRobo {
        class NadaDisponible(): OpcionRobo()
        class Robar(val hayPistas: Boolean, val hayCartas: Boolean) : OpcionRobo()
    }
}