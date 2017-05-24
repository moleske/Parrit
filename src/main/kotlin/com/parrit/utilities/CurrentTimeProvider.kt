package com.parrit.utilities

import org.springframework.stereotype.Component

import java.sql.Timestamp

@Component
class CurrentTimeProvider {

    val currentTime: Timestamp
        get() = Timestamp(System.currentTimeMillis())

}
