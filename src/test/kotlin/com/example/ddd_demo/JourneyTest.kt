package com.example.ddd_demo

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class JourneyTest @Autowired constructor(private val client: WebTestClient) {
    
    @Test
    fun `journey test`() {
        val assemblingChecklistLocation: String = startAssemblingCar()
        val carId = parseCarId(assemblingChecklistLocation)
        
        var tasksCompletedCount = getTasksCompletedCount(carId)
        tasksCompletedCount shouldBe 0
        
        markTaskCompleted(carId, taskIndex = 0)
        
        tasksCompletedCount = getTasksCompletedCount(carId)
        tasksCompletedCount shouldBe 1
        
        markTaskCompleted(carId, taskIndex = 1)
        
        tasksCompletedCount = getTasksCompletedCount(carId)
        tasksCompletedCount shouldBe 2
    }

    private fun markTaskCompleted(carId: UUID, taskIndex: Int) {
        client
            .put().uri("/api/v1/assembly/$carId/tasks/$taskIndex/complete")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "completedBy": "santi"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
    }

    private fun getTasksCompletedCount(carId: UUID): Int = client
        .get().uri("/api/v1/assembly/$carId/status")
        .exchange()
        .expectStatus().isOk
        .returnResult<AssemblyStatusResponse>()
        .responseBody.blockFirst()!!.tasksCompletedCount
    
    private fun parseCarId(assemblingChecklistLocation: String): UUID {
        val carId = assemblingChecklistLocation.substringAfterLast("/")
        return UUID.fromString(carId)
    }
    
    private fun startAssemblingCar(): String = client
        .post().uri("/api/v1/assembly/start")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            """
            {
                "carModel": {
                    "make": "some make",
                    "trim": "some trim"
                },
                "customizations": [
                    {
                        "description": "some description"
                    },
                    {
                        "description": "some other description"
                    }
                ]
            }
            """.trimIndent()
        )
        .exchange()
        .expectStatus().isCreated
        .expectHeader().exists("Location")
        .returnResult<String>()
        .responseHeaders["Location"]!!.first()

    companion object {
        @Container
        @ServiceConnection
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15-alpine")
            .withUsername("some-username")
            .withPassword("some-password")
    }
}

data class AssemblyStatusResponse(val tasksCompletedCount: Int)
