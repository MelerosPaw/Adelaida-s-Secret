package com.example.composetest.extensions

import android.content.Intent

fun Intent.getLongOrNull(key: String): Long? = extras?.takeIf { it.containsKey(key) }?.getLong(key)

fun Intent.putNotNullExtra(key: String, long: Long?): Intent {
    long?.let { putExtra(key, it) }
    return this
}