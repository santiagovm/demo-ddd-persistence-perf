package com.example.ddd_demo.application

import com.example.ddd_demo.domain.CarAssemblyChecklist
import com.example.ddd_demo.domain.CarAssemblyChecklistRepository
import com.example.ddd_demo.domain.CarAssemblyTask
import com.example.ddd_demo.domain.CarAssemblyTasksFactory
import com.example.ddd_demo.domain.TasksWrapper
import com.example.ddd_demo.web.StartAssemblingCarRequest
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class StartAssemblingCarUseCase(
    private val carAssemblyChecklistRepository: CarAssemblyChecklistRepository,
    private val carAssemblyTasksFactory: CarAssemblyTasksFactory,
) {
    private val log = LoggerFactory.getLogger(StartAssemblingCarUseCase::class.java)

    @Transactional
    @Retryable(
        value = [PSQLException::class],
        maxAttempts = 5,
        backoff = Backoff(delay = 250) // 250 ms delay between retries
    )
    fun execute(request: StartAssemblingCarRequest): UUID {
        carAssemblyChecklistRepository
            .findById(request.carChecklistId)
            .ifPresentOrElse(
                {
                    log.info(" >>> Car Checklist already exists: [${request.carChecklistId}]")
                },
                {
                    val carModel = request.carModel
                    val customizations = request.customizations
                    val carAssemblyTasks: List<CarAssemblyTask> =
                        carAssemblyTasksFactory.create(carModel, customizations)
                    val tasksWrapper = TasksWrapper(carAssemblyTasks)
                    val checklist = CarAssemblyChecklist(
                        id = request.carChecklistId,
                        tasks = tasksWrapper,
                    )
                    carAssemblyChecklistRepository.save(checklist)
                    log.info(" >>> Car Checklist created: [${checklist.id}]")
                }
            )

        return request.carChecklistId
    }
}
