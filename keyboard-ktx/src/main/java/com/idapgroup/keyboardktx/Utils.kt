package com.idapgroup.keyboardktx

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver

internal fun uiHandler() = Handler(Looper.getMainLooper())

internal fun Handler.postWithInterval(
    delayMillis: Long,
    block: () -> Unit
): Cancellable {
    val runnable = object: Runnable {
        override fun run() {
            block()
            postDelayed(this, delayMillis)
        }
    }
    runnable.run()

    return SafeCancellable { removeCallbacks(runnable) }
}


internal val Context.isDebugMode: Boolean
    get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

internal fun Context?.asActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.asActivity()
        else -> null
    }
}

internal fun View.onGlobalLayout(
    block: () -> Unit
): Cancellable {
    val listener = ViewTreeObserver.OnGlobalLayoutListener { block() }
    viewTreeObserver.addOnGlobalLayoutListener(listener)

    return SafeCancellable {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}