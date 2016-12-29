package com.yo1000.dimg

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 * @author yo1000
 */
@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
