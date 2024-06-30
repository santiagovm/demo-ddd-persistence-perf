package com.example.ddd_demo.domain

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface CarAssemblyChecklistRepository : CrudRepository<CarAssemblyChecklist, UUID> {
}