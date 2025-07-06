package com.example.composetest.ui.viewmodel

import android.content.Context
import androidx.annotation.StringRes

class ResourceManager(private val context: Context) {

    fun getText(@StringRes stringId: Int): CharSequence = context.getText(stringId)

    fun getString(@StringRes stringId: Int, vararg args: Any): String =
        context.getString(stringId, *args)

}