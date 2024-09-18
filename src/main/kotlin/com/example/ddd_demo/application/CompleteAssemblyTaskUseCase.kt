package com.example.ddd_demo.application

import com.example.ddd_demo.domain.CarAssemblyChecklist
import com.example.ddd_demo.domain.CarAssemblyChecklistRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CompleteAssemblyTaskUseCase(private val repository: CarAssemblyChecklistRepository) {

    private val log = LoggerFactory.getLogger(CompleteAssemblyTaskUseCase::class.java)

    @Transactional
    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 10,
        backoff = Backoff(
            delay = 200,
            maxDelay = 3000,
            multiplier = 1.3,
        )
    )
    fun execute(carChecklistId: UUID, taskIndex: Int, completedBy: String) {
        val checklist: CarAssemblyChecklist = repository.findById(carChecklistId).orElseThrow()
        checklist.completeTask(taskIndex, completedBy)
        repository.save(checklist)
        log.info(" >>> task completed, index: [$taskIndex], checklist: [$carChecklistId]")
    }
}
