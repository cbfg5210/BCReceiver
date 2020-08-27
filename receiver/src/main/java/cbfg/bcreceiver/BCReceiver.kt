package cbfg.bcreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/10 14:42
 * 功能描述：广播接收器封装
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/10 14:42
 * 修改内容：
 */
class BCReceiver : BroadcastReceiver() {
    private val intentFilter: IntentFilter by lazy { IntentFilter() }
    private lateinit var callback: ((context: Context, intent: Intent) -> Unit)
    private var bcWatcher: BCWatcher? = null
    private var hasRegisterReceiver = false

    override fun onReceive(context: Context, intent: Intent) {
        callback(context, intent)
    }

    fun withFilter(filter: (IntentFilter) -> Unit): BCReceiver {
        filter(intentFilter)
        return this
    }

    fun setCallback(callback: (context: Context, intent: Intent) -> Unit): BCReceiver {
        this.callback = callback
        return this
    }

    fun setBCWatcher(bcWatcher: BCWatcher): BCReceiver {
        this.bcWatcher = bcWatcher
        this.callback = bcWatcher.create()
        return this
    }

    fun register(context: Context) {
        if (!hasRegisterReceiver) {
            bcWatcher?.triggerAtOnce(context)
            context.registerReceiver(this, intentFilter)
            hasRegisterReceiver = true
        }
    }

    fun unregister(context: Context) {
        if (hasRegisterReceiver) {
            context.unregisterReceiver(this)
            hasRegisterReceiver = false
        }
    }

    /**
     * for AppCompatActivity, Fragment, LifecycleService
     */
    fun bind(
        context: Context,
        lifecycle: Lifecycle,
        event: Lifecycle.Event = Lifecycle.Event.ON_CREATE
    ) {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                checkRegister(context, event == Lifecycle.Event.ON_CREATE)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                checkRegister(context, event == Lifecycle.Event.ON_START)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                checkRegister(context, event == Lifecycle.Event.ON_RESUME)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                checkUnregister(context, event == Lifecycle.Event.ON_RESUME)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                checkUnregister(context, event == Lifecycle.Event.ON_START)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                checkUnregister(context, event == Lifecycle.Event.ON_CREATE)
            }
        })
    }

    private fun checkRegister(context: Context, shouldRegister: Boolean) {
        if (shouldRegister) {
            register(context)
        }
    }

    private fun checkUnregister(context: Context, shouldUnregister: Boolean) {
        if (shouldUnregister) {
            unregister(context)
        }
    }
}