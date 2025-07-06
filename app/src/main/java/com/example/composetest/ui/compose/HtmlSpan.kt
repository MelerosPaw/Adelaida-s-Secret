package com.example.composetest.ui.compose

import android.graphics.Typeface
import android.text.Html
import android.text.style.StyleSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

class HtmlSpan(text: String) {

    // El modo COMPACT hace que los <p> solo metan un solo salto de línea en lugar de dos como el LEGACY.
    private val html = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    val text: String = html.toString()
    val spans: List<AnnotatedString.Range<SpanStyle>> = html.getSpans(0, html.length, StyleSpan::class.java)
            .mapNotNull { span ->
                val style = when (span.style) {
                    Typeface.ITALIC -> { SpanStyle(fontStyle = FontStyle.Italic) }
                    Typeface.BOLD -> { SpanStyle(fontWeight = FontWeight.Bold) }
                    Typeface.BOLD_ITALIC -> { SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold) }
                    else -> null
                }

                style?.let {
                    val start = html.getSpanStart(span)
                    val end = html.getSpanEnd(span)
                    AnnotatedString.Range(it, start, end)
                }
            }
}