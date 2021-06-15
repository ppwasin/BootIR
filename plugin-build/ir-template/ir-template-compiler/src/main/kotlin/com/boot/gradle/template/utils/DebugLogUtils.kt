package com.boot.gradle.template.utils

import com.boot.gradle.template.DebugLog

object DebugLogUtils {
    fun getDebugLogReference(): String {
        return DebugLog::class.toString().split(" ")[1]
    }
}