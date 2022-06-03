package com.lyni.file.lib.excuter

import com.script.javascript.RhinoScriptEngine

/**
 * @date 2022/6/3
 * @author Liangyong Ni
 * description Constants
 */
object Constants {
    val SCRIPT_ENGINE: RhinoScriptEngine by lazy {
        RhinoScriptEngine()
    }
}