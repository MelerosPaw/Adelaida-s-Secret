package com.example.composetest.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.composetest.data.uc.None
import com.example.composetest.data.uc.ObtenerSospechososUC
import com.example.composetest.model.Sospechoso
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SospechososViewModel @Inject constructor(): BaseViewModel() {

    @Inject
    lateinit var obtenerSospechososUC: ObtenerSospechososUC

    private val _sospechosos: MutableState<List<Sospechoso>> = mutableStateOf(emptyList())
    val sospechosos: State<List<Sospechoso>> = _sospechosos

    fun cargarSospechosos() {
        if (sospechosos.value.isEmpty()) {
            suspender {
                _sospechosos.value = obtenerSospechososUC(None)
            }
        }
    }
}