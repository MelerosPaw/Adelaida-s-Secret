package com.example.composetest.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetest.model.Rasgo
import com.example.composetest.model.Sospechoso
import com.example.composetest.ui.compose.sampledata.sospechosos
import com.example.composetest.ui.compose.screen.ScreenPreviewMarron
import com.example.composetest.ui.compose.theme.FondoTarjetaSospechoso
import com.example.composetest.ui.compose.theme.MargenEstandar
import com.example.composetest.ui.compose.widget.AdelaidaText
import com.example.composetest.ui.compose.widget.GridItem
import com.example.composetest.ui.compose.widget.NivelTitulo
import com.example.composetest.ui.compose.widget.SeparadorGrid
import com.example.composetest.ui.compose.widget.Titulo

@Composable
fun LibroSecretos(sospechosos: List<Sospechoso>, columnas: Int, onSospechosoClicado: (Sospechoso) -> Unit) {
    LazyVerticalGrid(GridCells.Fixed(columnas)) {
        itemsIndexed(sospechosos, { _, it -> it.nombre }) { index, it ->
            val separador = SeparadorGrid(columnas = columnas, index, sospechosos.size,
                SeparadorGrid.InfoSeparadores(left = 5.dp, top = 5.dp, right = 5.dp, bottom = 5.dp))

            GridItem(separador) {
                TarjetaSospechoso(it, onSospechosoClicado)
            }
        }
    }
}

@Composable
fun TarjetaSospechoso(
    sospechoso: Sospechoso,
    onSospechosoClicado: (Sospechoso) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(FondoTarjetaSospechoso)
            .clickable { onSospechosoClicado(sospechoso) }
            .padding(MargenEstandar)
    ) {
        Titulo(sospechoso.nombre, nivel = NivelTitulo.Nivel2)
        Titulo("Rasgos", nivel = NivelTitulo.Nivel3)
        ListaRasgos(sospechoso.rasgos)
        Titulo("Secreto", Modifier.padding(top = 8.dp), nivel = NivelTitulo.Nivel3)
        Secreto(sospechoso)
    }
}

@Composable
fun ListaRasgos(rasgos: Array<Rasgo>) {
    rasgos.forEach {
        val texto = HtmlSpan("\t* ${it.textoEnLibro}")
        AdelaidaText(texto.text, spans = texto.spans)
    }
}

@Composable
private fun Secreto(sospechoso: Sospechoso) {
    val texto = HtmlSpan(sospechoso.secreto.textoEnLibro)
    AdelaidaText(texto.text, spans = texto.spans, inicialDecorativa = true)
}

@Preview
@Composable
private fun PreviewTarjeta() {
    ScreenPreviewMarron {
        LibroSecretos(sospechosos(10), 1, {})
    }
}