# BCReceiver
[![](https://jitpack.io/v/com.gitee.cbfg5210/BCReceiver.svg)](https://jitpack.io/#com.gitee.cbfg5210/BCReceiver)


为了简化广播接收处理，封装了一下广播接收器，使用方法：

## 引入依赖
### Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
	repositories {
	  ...
	  maven { url 'https://jitpack.io' }
    }
}
```
### Step 2. Add the dependency
```gradle
dependencies {
	implementation 'com.gitee.cbfg5210:BCReceiver:$version'
}
```

## 使用

### 1、普通用法
```java
    BCReceiver()
		// 添加 action 等
		.withFilter { intentFilter ->
			intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
			intentFilter.addAction(Intent.ACTION_TIME_TICK)
		}
		// 设置回调
		.setCallback { context, intent -> Log.e("***", "${System.currentTimeMillis()}") }
		// 自定义回调处理
		//.setBCWatcher(BCWatcher)
		//.bind(this, lifecycle) // 默认在 onCreate 注册广播接收器,在 onDestroy 注销
		//.bind(this,lifecycle,Lifecycle.Event.ON_START) // 在 onStart 注册广播接收器,在 onStop 注销
		.bind(this, lifecycle, Lifecycle.Event.ON_RESUME) // 在 onResume 注册广播接收器,在 onPause 注销
```

### 2、自定义回调处理

以时间广播为例，我们在收到时间广播后，往往需要获取当前的时间并且对其进行格式化，这时候可以实现 BCWatcher 的接口，
在其中对时间进行处理再对外提供以外回调方法以供使用即可。

[BCWatcher.kt](https://gitee.com/cbfg5210/BCReceiver/blob/master/receiver/src/main/java/cbfg/bcreceiver/BCWatcher.kt) :

```java
interface BCWatcher {
    /**
     * 创建 BCReceiver 广播接收器回调
     */
    fun create(): (context: Context, intent: Intent) -> Unit

    /**
     * 注册广播接收器时调用
     */
    fun triggerAtOnce(context: Context)
}
```

[TimeWatcher.kt](https://gitee.com/cbfg5210/BCReceiver/blob/master/receiver/src/main/java/cbfg/bcreceiver/watcher/TimeWatcher.kt) :

```java
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
```

#### 使用 TimeWatcher:

```java
   BCReceiver()
          .withFilter { intentFilter ->
              intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
              intentFilter.addAction(Intent.ACTION_TIME_TICK)
          }
           .setBCWatcher(TimeWatcher("yyyy-MM-dd HH:mm:ss") { timeMills, formattedTime ->
	       Log.e("***", "timeMills=$timeMills,formattedTime=$formattedTime")
	   })
          .bind(this, lifecycle)
```

为了便利使用以及减少重复代码，依赖库中对时间广播、home 键广播、电量广播、网络广播自定义了回调处理:
![capture_1.png](https://raw.githubusercontent.com/cbfg5210/BroadcastReceiver/master/captures/capture_1.png)

具体使用可以看[这里](https://gitee.com/cbfg5210/BCReceiver/blob/master/app/src/main/java/cbfg/bcreceiver/MainActivity.kt)