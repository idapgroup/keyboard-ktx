package com.idapgroup.keyboardktx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * Something, usually a task, that can be cancelled. Cancellation is performed by the cancel method.
 * Additional method is provided to determine if the task was cancelled.
 */
interface Cancellable {
    val isCancelled: Boolean
    fun cancel()
}

/**
 * Take care of a single cancellation of [call], following the [cancel] invocation will be ignored
 */
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

/**
 * Cancels [this] [Cancellable] when  lifecycleOwner] emits [cancelEvent]
 * @param lifecycleOwner lifecycle events emitter
 * @param cancelEvent by which [this] [Cancellable] will be cancelled
 */
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

/**
 * Cancels [this] [Cancellable] when  [lifecycleOwner] emits [Lifecycle.Event.ON_DESTROY] event
 * @param lifecycleOwner lifecycle events emitter
 */
fun Cancellable.cancelWhenDestroyed(lifecycleOwner: LifecycleOwner) {
    cancelWhen(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
}
