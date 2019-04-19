package com.idapgroup.keyboardktx

import android.view.View

internal class ShowChangedController(
    private val onShowChanged: OnKeyboardShowChanged
) {
    private var shown: Boolean? = null
    private var inAppHeight: Int = 0

    fun update(anyView: View) {
        val shown = anyView.isKeyboardShown
        val inAppHeight = anyView.keyboardHeight

        if (this.shown == null
            || this.shown != shown
            || this.inAppHeight != inAppHeight
        ) {
            this.shown = shown
            this.inAppHeight = inAppHeight
            onShowChanged(shown, inAppHeight)

        }
    }
}