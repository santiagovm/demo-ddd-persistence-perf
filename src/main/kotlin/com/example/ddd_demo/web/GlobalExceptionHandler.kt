package com.example.ddd_demo.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.NoSuchElementException

@ControllerAdvice
class GlobalExceptionHandler {

    // Handle NoSuchElementException and return HTTP 404
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity("Resource not found", HttpStatus.NOT_FOUND)
    }
}
