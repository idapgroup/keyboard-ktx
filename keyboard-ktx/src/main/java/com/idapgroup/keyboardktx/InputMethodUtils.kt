package com.idapgroup.keyboardktx

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import java.lang.reflect.InvocationTargetException


internal val Context.inputMethodManager: InputMethodManager
    get() {
        return getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

internal val Activity.softInputMode: Int get() = window.attributes.softInputMode

internal val View.inAppKeyboardHeight: Int
    get() {
        checkCanDefineKeyboardHeight()
        val rootView = rootView as ViewGroup
        return rootView.getChildAt(0).paddingBottom
    }

internal fun View.checkCanDefineKeyboardHeight() {
    val softInputMode = context.asActivity()?.softInputMode
    if (context.isDebugMode && softInputMode != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) {
        throw RuntimeException(
            "Can't define keyboard visible height for soft input mode != SOFT_INPUT_ADJUST_RESIZE.")
    }
}

internal fun View.hideInternal() {
    windowToken?.let {
        context.inputMethodManager.hideSoftInputFromWindow(it, 0)
    }
}

internal val Context.inputMethodWindowVisibleHeight: Int?
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
internal fun <T> callMethod(target: Any, methodName: String): T {
    val method = target.javaClass.getDeclaredMethod(methodName)
    method.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return method.invoke(target) as T
}