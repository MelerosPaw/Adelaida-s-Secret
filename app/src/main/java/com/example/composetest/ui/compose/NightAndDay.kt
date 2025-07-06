package com.example.composetest.ui.compose

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Claro")
@Preview(name = "Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class NightAndDay()