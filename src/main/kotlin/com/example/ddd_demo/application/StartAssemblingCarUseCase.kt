package com.example.ddd_demo.application

import com.example.ddd_demo.domain.CarAssemblyChecklist
import com.example.ddd_demo.domain.CarAssemblyChecklistRepository
import com.example.ddd_demo.domain.CarAssemblyTask
import com.example.ddd_demo.domain.CarAssemblyTasksFactory
import com.example.ddd_demo.web.StartAssemblingCarRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StartAssemblingCarUseCase(
    private val carAssemblyChecklistRepository: CarAssemblyChecklistRepository,
    private val carAssemblyTasksFactory: CarAssemblyTasksFactory,
) {
    private val log = LoggerFactory.getLogger(StartAssemblingCarUseCase::class.java)
    
    fun execute(request: StartAssemblingCarRequest): UUID {
        val carModel = request.carModel
        val customizations = request.customizations
        val carAssemblyTasks: List<CarAssemblyTask> = carAssemblyTasksFactory.create(carModel, customizations)
        val checklist = CarAssemblyChecklist(
            id = request.carChecklistId,
            tasks = carAssemblyTasks
        )
        carAssemblyChecklistRepository.save(checklist)
        log.info(" >>> car assembly started: [${checklist.id}]")
        return checklist.id
    }
}
