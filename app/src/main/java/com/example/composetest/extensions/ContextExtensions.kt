package com.example.composetest.extensions

import android.content.Context
import androidx.annotation.PluralsRes

fun Context.getPlural(@PluralsRes resource: Int, howMany: Int, vararg args: Any): String =
  resources.getQuantityString(resource, howMany, *args)