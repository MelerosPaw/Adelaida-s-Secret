package com.example.composetest.ui.manager

import com.example.composetest.ui.viewmodel.TabJugadoresViewModel.MostrarElementos
import com.example.composetest.ui.viewmodel.TabJugadoresViewModel.OpcionesClicado

// TODO Melero: 20/10/24 Que no se calculen los elementos dentro de MatrizJugadores
sealed class AsuntoTurbio(val mostrarElementos: MostrarElementos, val opcionesClicado: OpcionesClicado) {
    class Compra(mostrarElementos: MostrarElementos) : AsuntoTurbio(mostrarElementos, OpcionesClicado.Compra())
    class Robo(mostrarElementos: MostrarElementos) : AsuntoTurbio(mostrarElementos, OpcionesClicado.Robo())
    class Ninguno(): AsuntoTurbio(MostrarElementos.ManoCompleta(), OpcionesClicado.OpcionesCasilla())
}