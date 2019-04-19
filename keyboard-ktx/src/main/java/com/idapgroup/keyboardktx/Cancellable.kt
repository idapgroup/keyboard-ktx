package com.idapgroup.keyboardktx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

interface Cancellable {
    val isCancelled: Boolean
    fun cancel()
}

class SafeCancellable(
    call: () -> Unit
): Cancellable {
    private var call: (() -> Unit)? = call

    override val isCancelled: Boolean
        get() = (call == null)

    override fun cancel() {
        if (!isCancelled) {
            call!!()
            call = null
        }
    }
}

fun Cancellable.cancelWhen(
    lifecycleOwner: LifecycleOwner,
    cancelEvent: Lifecycle.Event
){
    val lifecycleObserver = object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroyed() {
            cancelWhenMatch(Lifecycle.Event.ON_DESTROY)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop(){
            cancelWhenMatch(Lifecycle.Event.ON_STOP)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause(){
            cancelWhenMatch(Lifecycle.Event.ON_PAUSE)
        }

        fun cancelWhenMatch(event: Lifecycle.Event){
            if(cancelEvent == event){
                cancel()
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        }

    }
    lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
}

fun Cancellable.cancelWhenDestroyed(lifecycleOwner: LifecycleOwner){
    cancelWhen(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
}
