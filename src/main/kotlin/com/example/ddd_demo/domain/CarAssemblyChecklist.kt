package com.example.ddd_demo.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import java.time.Instant
import java.util.UUID

data class CarAssemblyChecklist(
    @Id val id: UUID,

    @MappedCollection(idColumn = "car_assembly_checklist_id", keyColumn = "id")
    val tasks: List<CarAssemblyTask>,
) {
    val completedTasksCount: Int
        get() = tasks.count { task -> task.completedOn != null }

    fun completeTask(taskIndex: Int, completedBy: String) {
        tasks[taskIndex].completeTask(completedBy)
    }

    @Column("created_on")
    private var _createdOn: Instant = Instant.now()
    val createdOn: Instant
        get() = _createdOn

    @Version
    private var version: Int = 0
}

data class CarAssemblyTask(
    val description: String,
) {
    fun completeTask(newCompletedBy: String) {
        completedOn = Instant.now()
        completedBy = newCompletedBy
    }

    @Column("completed_on")
    private var _completedOn: Instant? = null
    var completedOn: Instant?
        get() = _completedOn
        private set(value) {
            _completedOn = value
        }

    @Column("completed_by")
    private var _completedBy: String? = null
    var completedBy: String?
        get() = _completedBy
        private set(value) {
            _completedBy = value
        }
}
