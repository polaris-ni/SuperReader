package com.lyni.reader.lib.common.base.component

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver

/**
 * @date 2022/5/13
 * @author Liangyong Ni
 * description ProactiveLifecycleObserver. Just for observe proactively.
 */
interface ProactiveLifecycleObserver : LifecycleObserver {
    /**
     * Call the method to add an observer for lifecycle proactively.
     * No Other Actual Meaning.
     * @param lifecycle lifecycle that will be observed
     */
    fun observe(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }
}