package com.idapgroup.keyboardktx

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment

fun EditText.showKeyboard() {
    requestFocus()
    context.inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun View.hideKeyboard() {
    findFocus()?.hideInternal()
}

fun Activity.hideKeyboard() {
    currentFocus?.hideInternal()
}

/**
 * @return true if Keyboard shown on the screen.
 */
val Activity.isKeyboardShown: Boolean get() = keyboardHeight > 0

/**
 * @return true if Keyboard shown on the screen.
 */
val Fragment.isKeyboardShown: Boolean? get() = keyboardHeight?.let { it > 0 }

/**
 * @return true if Keyboard shown on the screen.
 */
val View.isKeyboardShown: Boolean get() = keyboardHeight > 0


val Activity.keyboardHeight: Int
    get() = inputMethodWindowVisibleHeight ?: window.decorView.inAppKeyboardHeight

val Fragment.keyboardHeight: Int?
    get() = context?.inputMethodWindowVisibleHeight ?: view?.inAppKeyboardHeight

val View.keyboardHeight: Int
    get() = context.inputMethodWindowVisibleHeight ?: inAppKeyboardHeight


typealias OnKeyboardShowChanged = (shown: Boolean, height: Int) -> Unit

/**
* @param checkDelayMillis - Only for case when soft input mode is not adjust resize and sdk >= 21
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
                "Can't define keyboard state for soft input mode != SOFT_INPUT_ADJUST_RESIZE and sdk < 21.")
        }
        uiHandler().postWithInterval(checkDelayMillis) {
            controller.update(view)
        }
    }
}






