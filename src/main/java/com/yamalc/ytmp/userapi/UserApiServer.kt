package com.yamalc.ytmp.userapi

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@EnableAutoConfiguration
@ComponentScan
class UserApiServer

fun main(args: Array<String>) {
    runApplication<UserApiServer>(*args)
}
