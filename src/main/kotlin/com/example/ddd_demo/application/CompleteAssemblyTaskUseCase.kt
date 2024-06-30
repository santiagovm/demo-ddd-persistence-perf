package com.example.ddd_demo.application

import com.example.ddd_demo.domain.CarAssemblyChecklist
import com.example.ddd_demo.domain.CarAssemblyChecklistRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CompleteAssemblyTaskUseCase(private val repository: CarAssemblyChecklistRepository) {
    private val log = LoggerFactory.getLogger(CompleteAssemblyTaskUseCase::class.java)
    
    fun execute(carId: UUID, taskIndex: Int, completedBy: String) {
        val checklist: CarAssemblyChecklist = repository.findById(carId).orElseThrow()
        checklist.completeTask(taskIndex, completedBy)
        repository.save(checklist)
        log.info(" >>> task completed, index: [$taskIndex]")
    }
}
