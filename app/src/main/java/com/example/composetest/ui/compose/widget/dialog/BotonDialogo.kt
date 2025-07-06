import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetest.R
import com.example.composetest.ui.compose.screen.PreviewFondo
import com.example.composetest.ui.compose.screen.PreviewFondoVerde
import com.example.composetest.ui.compose.theme.Tema
import com.example.composetest.ui.compose.theme.bodyFontFamily
import com.example.composetest.ui.compose.theme.displayFontFamily
import com.example.composetest.ui.compose.widget.AdelaidaDialogTextButton
import com.example.composetest.ui.compose.widget.AdelaidaText
import java.util.Locale

@Composable
fun BotonDialogo(
    texto: String,
    enabled: Boolean = true,
    estilo: Estilo = Estilo.Minusculas(true),
    dimensiones: Dimensiones = Dimensiones.MatchParent,
    onClick: () -> Unit
) {
    AdelaidaDialogTextButton(onClick = onClick, enabled = enabled, modifier = Modifier.then(dimensiones.modifier)) {
        if (enabled && estilo.usaDecoracion) {
            Image(
                painterResource(R.drawable.bullet_point),
                null,
                Modifier.padding(end = 8.dp).height(BotonDialogoDefaults.BULLET_POINT_SIZE.dp),
                colorFilter = ColorFilter.tint(Tema.colors.contenidoDialogos),
            )
        }
        AdelaidaText(with(estilo) { texto.caps() }, fontSize = BotonDialogoDefaults.FONT_SIZE.sp,
            textAlign = TextAlign.Start, fontFamily = estilo.fontFamily,
            color = Tema.colors.contenidoDialogos.takeIf { enabled } ?: Tema.colors.textoBotonInhabilitado)
    }
}

class BotonDialogoDefaults {

    companion object {
        const val FONT_SIZE: Int = 16
        const val BULLET_POINT_SIZE = (FONT_SIZE + 4)
    }
}

sealed class Estilo(
    val fontFamily: FontFamily,
    val caps: String.() -> String,
    val usaDecoracion: Boolean
) {
    class Minusculas(usaDecoracion: Boolean): Estilo(displayFontFamily, { replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }, usaDecoracion)
    class Mayusculas(usaDecoracion: Boolean): Estilo(bodyFontFamily, { uppercase() }, usaDecoracion)
}

sealed class Dimensiones(val modifier: Modifier) {
    object WrapContent: Dimensiones(Modifier.wrapContentWidth())
    object MatchParent: Dimensiones(Modifier.fillMaxWidth())
}

@Composable
@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun P1() {
    Column {
        PreviewFondo {
            Column {
                BotonDialogo("Un botón de color marrón") { }
                BotonDialogo("Un botón inhabilitado", enabled = false) { }
            }
        }

        PreviewFondoVerde {
            Column {
                BotonDialogo("Un botón de color verde, con un texto bastante largo") { }
                BotonDialogo("Un botón de color verde inhabilitado", enabled = false) { }
            }
        }
    }
}
