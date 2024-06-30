package com.example.ddd_demo.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Version
import java.time.Instant
import java.util.NoSuchElementException
import java.util.UUID

@Entity
data class CarAssemblyChecklist(
    @Id val id: UUID,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "car_assembly_checklist_id")
    val tasks: List<CarAssemblyTask> = mutableListOf(),
) {
    val completedTasksCount: Int
        get() = tasks.count { task -> task.completedOn != null }

    fun completeTask(taskIndex: Int, completedBy: String) {
        val foundTask: CarAssemblyTask? = tasks.find { task -> task.taskIndex == taskIndex }
        
        if (foundTask == null) {
            throw NoSuchElementException("Task not found with index $taskIndex in checklist $id") 
        }
        
        foundTask.completeTask(completedBy)
    }

    @Column(name =  "created_on")
    private var _createdOn: Instant = Instant.now()
    val createdOn: Instant
        get() = _createdOn

    @Version
    private var version: Int = 0
}

@Entity
data class CarAssemblyTask(
    @Id val id: UUID = UUID.randomUUID(),
    val taskIndex: Int,
    val description: String,
) {
    fun completeTask(newCompletedBy: String) {
        completedOn = Instant.now()
        completedBy = newCompletedBy
    }

    @Column(name =  "completed_on")
    private var _completedOn: Instant? = null
    var completedOn: Instant?
        get() = _completedOn
        private set(value) {
            _completedOn = value
        }

    @Column(name =  "completed_by")
    private var _completedBy: String? = null
    var completedBy: String?
        get() = _completedBy
        private set(value) {
            _completedBy = value
        }

    @Version
    private var version: Int = 0
}
