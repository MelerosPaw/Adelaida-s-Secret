package com.example.composetest.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun Int.get(): String = stringResource(this)

@Composable
fun Int.get(vararg args: Any): String = stringResource(this, *args)