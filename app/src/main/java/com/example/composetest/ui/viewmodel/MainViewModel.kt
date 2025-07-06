package com.example.composetest.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composetest.data.uc.ComprobarCargaInicialUC
import com.example.composetest.data.uc.None
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val comprobarCargaInicialUC: ComprobarCargaInicialUC
): BaseViewModel() {

    private val _datosInicialesLiveData = MutableLiveData<Boolean>()
    val datosInicialesLiveData: LiveData<Boolean> = _datosInicialesLiveData

    fun comprobaDatosIniciales() {
        suspender {
            _datosInicialesLiveData.value = comprobarCargaInicialUC(None)
        }
    }
}