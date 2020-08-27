package cbfg.bcreceiver.watcher

import android.content.Context
import android.content.Intent
import cbfg.bcreceiver.BCWatcher
import java.text.SimpleDateFormat
import java.util.*

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/10 16:15
 * 功能描述：时间广播接收器回调
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/10 16:15
 * 修改内容：
 */
class TimeWatcher(
    format: String? = null,
    locale: Locale? = null,
    private val action: (timeMills: Long, formattedTime: String?) -> Unit
) : BCWatcher {
    private var dateFormat = format?.run { SimpleDateFormat(this, locale ?: Locale.getDefault()) }

    override fun create(): (context: Context, intent: Intent) -> Unit {
        return { context, _ -> triggerAtOnce(context) }
    }

    override fun triggerAtOnce(context: Context) {
        val timeMills = System.currentTimeMillis()
        val formattedTime = dateFormat?.format(timeMills)
        action(timeMills, formattedTime)
    }
}