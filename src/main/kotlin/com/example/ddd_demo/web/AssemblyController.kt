package com.example.ddd_demo.web

import com.example.ddd_demo.application.CompleteAssemblyTaskUseCase
import com.example.ddd_demo.application.StartAssemblingCarUseCase
import com.example.ddd_demo.domain.CarAssemblyChecklistRepository
import com.example.ddd_demo.domain.CarCustomization
import com.example.ddd_demo.domain.CarModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/v1/assembly")
class AssemblyController(
    private val startAssemblingCarUseCase: StartAssemblingCarUseCase,
    private val completeAssemblyTaskUseCase: CompleteAssemblyTaskUseCase,
    private val carAssemblyChecklistRepository: CarAssemblyChecklistRepository,
) {
    @PostMapping("/start")
    fun startAssemblingCar(
        @RequestBody request: StartAssemblingCarRequest,
        uriComponentsBuilder: UriComponentsBuilder,
    ): ResponseEntity<Unit> {
        startAssemblingCarUseCase.execute(request)
        val location = uriComponentsBuilder
            .path("/api/v1/assembly/{id}")
            .buildAndExpand(request.carChecklistId)
            .toUri()
        return ResponseEntity.created(location).build()
    }

    @GetMapping("/{carId}/status")
    fun getStatus(@PathVariable carId: UUID): ResponseEntity<AssemblyStatusResponse> =
        carAssemblyChecklistRepository.findById(carId)
            .map { checklist ->
                val assemblyStatusResponse = AssemblyStatusResponse(checklist.completedTasksCount)
                ResponseEntity.ok(assemblyStatusResponse)
            }.orElseGet {
                ResponseEntity.notFound().build()
            }

    @PutMapping("/{carId}/tasks/{taskIndex}/complete")
    fun completeTask(
        @PathVariable carId: UUID,
        @PathVariable taskIndex: Int,
        @RequestBody request: CompleteTaskRequest,
    ): ResponseEntity<Unit> {
        completeAssemblyTaskUseCase.execute(carId, taskIndex, request.completedBy)
        return ResponseEntity.ok().build()
    }
}

data class StartAssemblingCarRequest(
    val carChecklistId: UUID,
    val carModel: CarModel,
    val customizations: List<CarCustomization>
)

data class AssemblyStatusResponse(val tasksCompletedCount: Int)

data class CompleteTaskRequest(val completedBy: String)
