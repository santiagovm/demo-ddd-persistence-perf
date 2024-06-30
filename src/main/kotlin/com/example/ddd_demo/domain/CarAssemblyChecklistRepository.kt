package com.example.ddd_demo.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CarAssemblyChecklistRepository : JpaRepository<CarAssemblyChecklist, UUID> {
}
