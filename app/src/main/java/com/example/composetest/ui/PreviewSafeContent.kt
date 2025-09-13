package com.example.composetest.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

@Composable
fun PreviewSafeContent(previewContent: @Composable () -> Unit, content: @Composable () -> Unit) {
  if (LocalView.current.isInEditMode) {
    previewContent()
  } else {
    content()
  }
}