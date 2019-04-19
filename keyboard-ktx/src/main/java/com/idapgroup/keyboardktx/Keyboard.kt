package com.idapgroup.keyboardktx

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment

/**
 *  Shows keyboard for EditText
 */
fun EditText.showKeyboard() {
    requestFocus()
    context.inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

/**
 *  Hides soft keyboard that is bound to the same [android.view.Window] that [this] [View]
 */
fun View.hideKeyboard() {
    findFocus()?.hideInternal()
}

/**
 *  Hides soft keyboard on Activity
 */
fun Activity.hideKeyboard() {
    currentFocus?.hideInternal()
}

/**
 * @return true if soft keyboard shown on the screen.
 */
val Activity.isKeyboardShown: Boolean get() = keyboardHeight > 0

/**
 * @return true if soft keyboard shown on the screen.
 */
val Fragment.isKeyboardShown: Boolean? get() = keyboardHeight?.let { it > 0 }

/**
 * @return true if Keyboard shown on the screen.
 */
val View.isKeyboardShown: Boolean get() = keyboardHeight > 0

/**
 * @return soft keyboard visible height in pixels on the screen.
 */
val Activity.keyboardHeight: Int
    get() = inputMethodWindowVisibleHeight ?: window.decorView.inAppKeyboardHeight

/**
 * @return soft keyboard visible height in pixels on the screen.
 */
val Fragment.keyboardHeight: Int?
    get() = context?.inputMethodWindowVisibleHeight ?: view?.inAppKeyboardHeight

/**
 * @return soft keyboard visible height in pixels on the screen.
 */
val View.keyboardHeight: Int
    get() = context.inputMethodWindowVisibleHeight ?: inAppKeyboardHeight

/**
 * @param shown true if soft keyboard shown on the screen.
 * @param height soft keyboard visible height in pixels on the screen.
 */
typealias OnKeyboardShowChanged = (shown: Boolean, height: Int) -> Unit

/**
 * @param onChanged triggered when soft keyboard shown state and height changed
 * @param checkDelayMillis - interval of changing soft keyboard show state.
 *        Only for case when soft input mode is not adjust resize and sdk >= 21
 */
fun Activity.onKeyboardShowChanged(
    checkDelayMillis: Long = 300,
    onChanged: OnKeyboardShowChanged
): Cancellable {
    val view = window.decorView
    val controller = ShowChangedController(onChanged)

    return if (softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) {
        controller.update(view)
        view.onGlobalLayout {
            controller.update(view)
        }
    } else {
        if (isDebugMode && Build.VERSION.SDK_INT < 21) {
            throw RuntimeException(
                "Can't define keyboard state for soft input mode != SOFT_INPUT_ADJUST_RESIZE and sdk < 21."
            )
        }
        uiHandler().postWithInterval(checkDelayMillis) {
            controller.update(view)
        }
    }
}






