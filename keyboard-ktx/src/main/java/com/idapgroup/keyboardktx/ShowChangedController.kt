package com.idapgroup.keyboardktx

import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener

internal class ShowChangedController(
    val onShowChanged: OnKeyboardShowChanged
) {
    var shown: Boolean? = null
    var inAppHeight: Int = 0

    fun update(anyView: View) {
        val shown = anyView.isKeyboardShown
        val onAppHeight = anyView.keyboardHeight
        if (this.shown == null || this.shown != shown
            || this.inAppHeight != onAppHeight
        ) {
            this.shown = shown
            this.inAppHeight = onAppHeight
            onShowChanged(shown, onAppHeight)

        }
    }
}