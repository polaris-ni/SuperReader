@file:Suppress("unused")

package io.legado.app.exception

import com.lyni.reader.lib.common.exceptions.NoStackTraceException

/**
 * 并发限制
 */
class ConcurrentException(msg: String, val waitTime: Int) : NoStackTraceException(msg)