package com.parrit

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ParritApplication

fun main(args: Array<String>) {
    SpringApplication.run(ParritApplication::class.java, *args)
}

