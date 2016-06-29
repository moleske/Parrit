package com.parrit.utilities

import org.springframework.stereotype.Component

import java.sql.Timestamp

@Component
open class CurrentTimeProvider {

//    what intellij converted to first.  why?
//    val currentTime: Timestamp
//        get() = Timestamp(System.currentTimeMillis())

    open fun getCurrentTime(): Timestamp {
        return Timestamp(System.currentTimeMillis())
    }

}
