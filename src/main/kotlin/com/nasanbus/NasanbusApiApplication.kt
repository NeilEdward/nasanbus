package com.nasanbus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NasanbusApiApplication

fun main(args: Array<String>) {
	runApplication<NasanbusApiApplication>(*args)
}
