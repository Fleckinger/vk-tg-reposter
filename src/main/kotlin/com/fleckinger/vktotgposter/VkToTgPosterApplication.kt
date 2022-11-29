package com.fleckinger.vktotgposter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class VkToTgPosterApplication
fun main(args: Array<String>) {
    runApplication<VkToTgPosterApplication>(*args)
}
