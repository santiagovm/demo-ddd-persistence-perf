package com.example.ddd_demo.domain

import org.springframework.stereotype.Component

@Component
class CarAssemblyTasksFactory {
    fun create(carModel: CarModel, customizations: List<CarCustomization>): List<CarAssemblyTask> {
        // fake implementation that creates a long list of tasks
        return (0..1_000).map { index ->
            CarAssemblyTask(description = "assembly task #$index",)
        }
    }
}
