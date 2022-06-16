package com.android.firebasechatapp.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Return the current timestamp in the form of a string
 * @return
 */
fun getTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("Canada/Pacific")
    return sdf.format(Date())
}