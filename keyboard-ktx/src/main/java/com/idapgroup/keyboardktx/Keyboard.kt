package com.idapgroup.keyboardktx

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import java.lang.reflect.InvocationTargetException

typealias OnKeyboardShowChanged = (shown: Boolean, height: Int) -> Unit

val Context.inputMethodManager: InputMethodManager
    get() {
        return getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }


fun View.hideKeyboard() {
    val focusedView = findFocus()
    if (focusedView != null) {
        hideInternal(context, focusedView.windowToken)
    }
}

fun View.showKeyboard() {
    requestFocus()
    context.inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun Activity.hideKeyboard() {
    val focusedView = currentFocus
    if (focusedView != null) {
        hideInternal(focusedView.context, focusedView.windowToken)
    }
}

fun Activity.onKeyboardShowChanged(onChanged: OnKeyboardShowChanged): Cancellable {
    val anyView = window.decorView
    val showChangeContext = ShowChangedContext(onChanged)
    showChangeContext.notifyListener(anyView)

    val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        showChangeContext.notifyListener(anyView)
    }
    anyView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

    return ShowChangedCancellable(anyView, globalLayoutListener)
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


private val View.inAppKeyboardHeight: Int
    get() {
        checkCanDefineKeyboardHeight()
        val rootView = rootView as ViewGroup
        return rootView.getChildAt(0).paddingBottom
    }

private fun View.checkCanDefineKeyboardHeight() {
    val softInputMode = context.asActivity()?.window?.attributes?.softInputMode
    if (softInputMode != SOFT_INPUT_ADJUST_RESIZE) {
        throw RuntimeException(
            "Can't define keyboard visible height for soft input mode != SOFT_INPUT_ADJUST_RESIZE."
        )
    }
}

private fun hideInternal(ctx: Context, windowToken: IBinder?) {
    if (windowToken != null) {
        ctx.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}

private val Context.inputMethodWindowVisibleHeight: Int?
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    get() = try {
        callMethod(inputMethodManager, "getInputMethodWindowVisibleHeight")
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
        null
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
        null
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
        null
    }

@Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
private fun <T> callMethod(target: Any, methodName: String): T {
    val method = target.javaClass.getDeclaredMethod(methodName)
    method.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return method.invoke(target) as T
}

private class ShowChangedCancellable(
    var view: View?,
    var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener?
) : Cancellable() {

    override fun cancel() {
        if (view != null) {
            view!!.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            view = null
            globalLayoutListener = null
        }
    }
}

private class ShowChangedContext(val onShowChanged: OnKeyboardShowChanged) {
    var shown: Boolean? = null
    var onAppHeight: Int = 0

    fun notifyListener(anyView: View) {
        val shown = anyView.isKeyboardShown
        val onAppHeight = anyView.keyboardHeight
        if (this.shown == null || this.shown != shown
            || this.onAppHeight != onAppHeight
        ) {
            this.shown = shown
            this.onAppHeight = onAppHeight
            onShowChanged(shown, onAppHeight)

        }
    }
}


private fun Context?.asActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.asActivity()
        else -> null
    }
}


open class Cancellable(val call: () -> Unit = {}) {

    protected var called = false

    open fun cancel() {
        if (!called) {
            call()
            called = true
        }
    }
}

