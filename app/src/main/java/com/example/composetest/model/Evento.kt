package com.example.composetest.model

/**
 * @property ronda Ronda en la que se ejecutará el evento.
 * @property nombre
 * @property explicacion Texto que el maestro del juego debe leer a los jugadores.
 * @property maxGanadores Cantidad de personas que se pueden ver afectadas por la acción: cuánta
 * gente gana el comodín o cuánta gente sufre el efecto del evento si el efecto aplica a gente.
 * @property accion La acción que sucede, que puede ser un efecto inmediato (se aplicará en el
 * momento en que se ejecute el evento) o la obtención de un comodín.
 */
data class Evento(
    val ronda: Partida.Ronda,
    val nombre: String,
    val explicacion: String,
    val puntuaciones: String,
    val maxGanadores: MaxGanadores,
    val accion: Accion,
) {

    override fun equals(other: Any?): Boolean = (other as? Evento)?.nombre == nombre

    override fun hashCode(): Int {
        var result = ronda.hashCode()
        result = 31 * result + nombre.hashCode()
        return result
    }

    fun obtenerMaxGanadores() : Int = maxGanadores.obtenerCantidadGanadores()

    sealed class MaxGanadores(val cantidad: String) {

        override fun toString(): String = cantidad

        fun obtenerCantidadGanadores() : Int = when (this) {
            is Todos -> Int.MAX_VALUE
            is CantidadDeterminada -> this.cantidad.toIntOrNull() ?: Int.MAX_VALUE
            is Nadie -> 0
        }

        class CantidadDeterminada(cantidad: String) : MaxGanadores(cantidad)
        object Todos: MaxGanadores("Todos")
        object Nadie: MaxGanadores("Nadie")
    }

    sealed class Accion {
        class OtorgarComodin(val comodin: Comodin): Accion()
        class AplicarEfecto(val efecto: Efecto): Accion()
    }

    sealed class Efecto(val id: String, val explicacion: String) {
        object DobleInvestigacion: Efecto("EF1", "El jugador investiga dos veces (en la misma o en distinta habitación)")
        object DobleRobo: Efecto("EF2", "Puedes robar dos objetos al usar un brandy")
        object DobleSicario: Efecto("EF3", "Puedes robar dos objetos con un solo sicario")
        object InhabilitadoTodoElDia: Efecto("EF4", "El jugador no juega ese día")
        object ProteccionContraAdelaida: Efecto("F5", "Protección contra Adelaida")
        object CancelarTarde: Efecto("EF6", "La ronda de asuntos turbios no tiene lugar ese día")
        object RevelarHabitacionElUltimo: Efecto("EF7", "Ser el último en revelar la habitación")
        object VerPistas: Efecto("EF8", "Puedes ver las ubicaciones de un tipo de pista o carta")
        object ProteccionContraAsuntosTurbios: Efecto("EF9", "No se puden jugar sicarios ni brandies contra este jugador.")
    }

    /**
     * @param comprobacionDeUsoAutomatica El comodín puede usarse cuando el jugador quiera, pero
     * puede ser que se use automáticamente cuando suceda determinada acción. De momento solo es el
     * caso del [InciensoProtector], que si se determina que Adelaida le visita, se pregunta
     * automáticamente si se quiere usar para evitar la visita.
     */
    sealed class Comodin(
        val id: String,
        val nombre: String,
        val efecto: Efecto,
        val rondaConsumo: Partida.Ronda,
        val comprobacionDeUsoAutomatica: Boolean
    ) {
        object GuantesBlancos: Comodin("CO1", "Guantes blancos", Efecto.DobleRobo, Partida.Ronda.TARDE, false)
        object DescuentoEnSicarios: Comodin("CO2", "2 x 1 en sicarios", Efecto.DobleSicario, Partida.Ronda.TARDE, false)
        object AcusacionFalsa: Comodin("CO3", "Acusación falsa", Efecto.InhabilitadoTodoElDia, Partida.Ronda.NOCHE, false)
        object InciensoProtector: Comodin("CO4", "Incienso protector", Efecto.ProteccionContraAdelaida, Partida.Ronda.NOCHE, true)
        object Somnifero: Comodin("CO5", "Somnífero", Efecto.InhabilitadoTodoElDia, Partida.Ronda.NOCHE, false)

        companion object {

            fun getById(id: String): Comodin? = listOf(
                GuantesBlancos,
                DescuentoEnSicarios,
                AcusacionFalsa,
                InciensoProtector,
                Somnifero
            ).firstOrNull { it.id == id }
        }
    }
}