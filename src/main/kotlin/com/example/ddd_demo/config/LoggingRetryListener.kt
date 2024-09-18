package com.example.ddd_demo.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component

@Component
class LoggingRetryListener : RetryListener {

    private val log = LoggerFactory.getLogger(LoggingRetryListener::class.java)
    
    override fun <T : Any?, E : Throwable?> onError(
        context: RetryContext?,
        callback: RetryCallback<T, E>?,
        throwable: Throwable?
    ) {
        log.info(" >>> error retry count [${context?.retryCount}]")
        super.onError(context, callback, throwable)
    }

    override fun <T : Any?, E : Throwable?> close(
        context: RetryContext?,
        callback: RetryCallback<T, E>?,
        throwable: Throwable?
    ) {
        if (throwable != null) {
            log.info(" >>> all retries failed after [${context?.retryCount}] attempts")
        }
        super.close(context, callback, throwable)
    }
}

@Configuration
class RetryConfig {
    
    @Bean
    fun retryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()
        retryTemplate.registerListener(LoggingRetryListener())
        return retryTemplate
    }
}
