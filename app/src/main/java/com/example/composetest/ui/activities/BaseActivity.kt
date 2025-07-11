package com.example.composetest.ui.activities

import android.app.AlertDialog
import androidx.activity.ComponentActivity
import com.example.composetest.ui.viewmodel.BaseViewModel

abstract class BaseActivity: ComponentActivity() {

    private lateinit var baseViewModel: BaseViewModel

    protected fun habilitarFuncionesBase(baseViewModel: BaseViewModel) {
        this.baseViewModel = baseViewModel

        baseViewModel.mostrarMensaje.observe(this) { mensaje ->
            mensaje?.let {
                AlertDialog.Builder(this)
                    .setTitle("Mensaje")
                    .setMessage(it)
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, _ -> dialog?.dismiss() }
                    .show()
            }
        }
    }
}