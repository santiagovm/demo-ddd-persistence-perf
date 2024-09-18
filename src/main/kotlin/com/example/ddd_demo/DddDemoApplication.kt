package com.example.ddd_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class DddDemoApplication

fun main(args: Array<String>) {
	runApplication<DddDemoApplication>(*args)
}
