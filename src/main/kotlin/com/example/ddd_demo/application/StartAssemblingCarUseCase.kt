package com.example.ddd_demo.application

import com.example.ddd_demo.domain.CarAssemblyChecklist
import com.example.ddd_demo.domain.CarAssemblyChecklistRepository
import com.example.ddd_demo.domain.CarAssemblyTask
import com.example.ddd_demo.domain.CarAssemblyTasksFactory
import com.example.ddd_demo.domain.CarCustomization
import com.example.ddd_demo.domain.CarModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StartAssemblingCarUseCase(
    private val carAssemblyChecklistRepository: CarAssemblyChecklistRepository,
    private val carAssemblyTasksFactory: CarAssemblyTasksFactory,
) {
    private val log = LoggerFactory.getLogger(StartAssemblingCarUseCase::class.java)
    
    fun execute(carModel: CarModel, customizations: List<CarCustomization>): UUID {
        val carAssemblyTasks: List<CarAssemblyTask> = carAssemblyTasksFactory.create(carModel, customizations)
        val checklist = CarAssemblyChecklist(tasks = carAssemblyTasks)
        carAssemblyChecklistRepository.save(checklist)
        log.info(" >>> car assembly started: [${checklist.id}]")
        return checklist.id
    }
}
