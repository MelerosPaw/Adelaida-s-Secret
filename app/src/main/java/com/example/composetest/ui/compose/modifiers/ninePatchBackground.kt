package com.example.composetest.ui.compose.modifiers

import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.composetest.R

@Composable
fun Modifier.ninePatchBackground(@DrawableRes ninePatchResourceId: Int, @ColorInt tint: Int? = null): Modifier {
  val context = LocalContext.current
  return drawBehind {
    drawIntoCanvas {
      ContextCompat.getDrawable(context, ninePatchResourceId)
        ?.let { ninePatch ->
          ninePatch.run {
            tint?.let(::setTint)
            bounds = Rect(0, 0, size.width.toInt(), size.height.toInt())
            draw(it.nativeCanvas)
          }
        }
    }
  }
}