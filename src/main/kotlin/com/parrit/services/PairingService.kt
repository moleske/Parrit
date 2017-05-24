package com.parrit.services

import com.parrit.entities.PairingHistory
import com.parrit.entities.Project
import com.parrit.repositories.PairingHistoryRepository
import com.parrit.repositories.ProjectRepository
import com.parrit.utilities.CurrentTimeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PairingService @Autowired
constructor(var pairingHistoryRepository: PairingHistoryRepository,
            var projectRepository: ProjectRepository,
            var recommendationService: RecommendationService,
            var currentTimeProvider: CurrentTimeProvider) {

    fun savePairing(projectId: Long): List<PairingHistory> {
        val pairingHistories = ArrayList<PairingHistory>()

        val project = projectRepository.findOne(projectId)
        val currentTime = currentTimeProvider.currentTime

        for (pairingBoard in project.pairingBoards) {
            val pairingBoardPeople = pairingBoard.people

            if (!pairingBoardPeople.isEmpty()) {
                val savedPairingHistory = pairingHistoryRepository.save(PairingHistory(project, ArrayList(pairingBoardPeople), currentTime, pairingBoard.name))
                pairingHistories.add(savedPairingHistory)
            }
        }

        return pairingHistories
    }

    fun getRecommendation(projectId: Long): Project? {
        val project = projectRepository.findOne(projectId)
        val pairingHistory = pairingHistoryRepository.findByProject(project)

        val recommendedProject: Project? = recommendationService.get(project, pairingHistory)

        projectRepository.save(recommendedProject)
        return recommendedProject
    }

    fun getSortedPairingHistory(projectId: Long): List<PairingHistory> {
        val project = projectRepository.findOne(projectId)
        return pairingHistoryRepository.findByProjectOrderByTimestampDesc(project)
    }
}
