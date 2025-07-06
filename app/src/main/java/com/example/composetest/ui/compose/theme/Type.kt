package com.example.composetest.ui.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.composetest.R

val displayFontFamily = FontFamily(Font(R.font.eczar))
val bodyFontFamily = FontFamily(Font(R.font.anaheim))

@Composable
fun adelaidaTypography(): Typography {
  val baseline = Typography()

  return Typography(
    displaySmall = baseline.displaySmall.copy(fontFamily = bodyFontFamily), // Display
    displayMedium = baseline.displayMedium.copy(fontFamily = bodyFontFamily), // Display
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = displayFontFamily), // Body
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = displayFontFamily), // Body
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
  )
}