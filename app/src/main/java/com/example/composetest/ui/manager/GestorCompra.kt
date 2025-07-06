package com.example.composetest.ui.manager

import com.example.composetest.model.ElementoTablero.Pista
import com.example.composetest.model.Jugador
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel.MostrarElementos

class GestorCompra(
    val idPartida: Long,
    val jugadores: List<Jugador>,
    val comprador: Jugador,
    val mostrarDialogoDinero: (AccionCompra) -> Unit,
    val ocultarDialogoDinero: () -> Unit,
    val ejecutarAsuntoTurbio: (AsuntoTurbio) -> Unit
) {

    fun comprarSicario() {
        mostrarDialogoDinero(getOpcionCompra(comprador))
    }

    fun comprarPista() {
        crearElementosParaComprar(MostrarElementos.PistasRasgos(comprador))
    }

    fun comprarSecreto() {
        crearElementosParaComprar(MostrarElementos.Secretos(comprador))
    }

    private fun getOpcionCompra(comprador: Jugador): AccionCompra {
        val opcionCompra = when {
            noTieneSuficienteDinero(comprador) -> OpcionCompra.DineroInsuficiente()
            noSeLePuedeComprarANadie(comprador) -> OpcionCompra.NadaDisponible()
            soloLeAlcanzaParaPistas(comprador) -> OpcionCompra.Pista(hayAlgunaPistaDeRasgos(comprador))
            else -> OpcionCompra.PistaOSecreto(hayAlgunaPistaDeRasgos(comprador), hayAlgunSecreto(comprador))
        }

        return AccionCompra(comprador, opcionCompra)
    }

    private fun hayAlgunaPistaDeRasgos(comprador: Jugador): Boolean = jugadores.any {
        it != comprador && it.manoComprable().any(MostrarElementos.PistasRasgos(it).predicado)
    }

    private fun hayAlgunSecreto(comprador: Jugador): Boolean = jugadores.any {
        it != comprador && it.manoComprable().any(MostrarElementos.Secretos(it).predicado)
    }

    private fun soloLeAlcanzaParaPistas(comprador: Jugador): Boolean =
        Pista.PRECIOS[Pista.Prefijo.SECRETO]?.let { comprador.dinero < it } == true

    private fun noSeLePuedeComprarANadie(comprador: Jugador): Boolean = jugadores.all {
        it == comprador || it.manoComprable().isEmpty()
    }

    private fun noTieneSuficienteDinero(comprador: Jugador): Boolean =
        comprador.dinero < Pista.PRECIO_MAS_BAJO

    private fun crearElementosParaComprar(mostrarElementos: MostrarElementos) {
        ejecutarAsuntoTurbio(AsuntoTurbio.Compra(mostrarElementos))
        ocultarDialogoDinero()
    }

    class AccionCompra(val comprador: Jugador, val opcion: OpcionCompra)

    sealed class OpcionCompra {
        class Pista(val hayPistas: Boolean) : OpcionCompra()
        class PistaOSecreto(val hayPistas: Boolean, val haySecretos: Boolean): OpcionCompra()
        class NadaDisponible(): OpcionCompra()
        class DineroInsuficiente(): OpcionCompra()
    }
}