package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.ui.compose.navegacion.NavegadorCreacion
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavegacionViewModel @Inject constructor(): BaseViewModel() {

    private val _configuracionToolbar: MutableState<NavegadorCreacion.ConfiguracionToolbar> =
        mutableStateOf(NavegadorCreacion.ConfiguracionToolbar())
    val configuracionToolbar: State<NavegadorCreacion.ConfiguracionToolbar> = _configuracionToolbar

    fun onConfiguracionToolbarCambiada(configuracionToolbar: NavegadorCreacion.ConfiguracionToolbar) {
        _configuracionToolbar.value = configuracionToolbar
    }
}