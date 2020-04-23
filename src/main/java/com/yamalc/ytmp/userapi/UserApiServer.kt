package com.yamalc.ytmp.userapi

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@EnableAutoConfiguration
@ComponentScan
class UserApiServer

fun main(args: Array<String>) {
//	val userClient = UserApiClient.create("localhost", 8081)
//	userClient.authenticate("user1", "password1")
    runApplication<UserApiServer>(*args)
}
