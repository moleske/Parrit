package com.parrit.repositories

import com.parrit.entities.PairingHistory
import com.parrit.entities.Project
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PairingHistoryRepository : CrudRepository<PairingHistory, Long> {

    fun findByProject(project: Project): List<PairingHistory>

    fun findByProjectOrderByTimestampDesc(project: Project): List<PairingHistory>
}
