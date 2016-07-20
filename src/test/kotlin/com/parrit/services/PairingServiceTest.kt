package com.parrit.services

import com.parrit.entities.PairingBoard
import com.parrit.entities.PairingHistory
import com.parrit.entities.Person
import com.parrit.entities.Project
import com.parrit.repositories.PairingHistoryRepository
import com.parrit.repositories.ProjectRepository
import com.parrit.utilities.CurrentTimeProvider
import io.damo.aspen.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.sql.Timestamp
import kotlin.test.assertEquals

class PairingServiceTest : Test() {

    lateinit var mockPairingHistoryRepository: PairingHistoryRepository
    lateinit var mockProjectRepository: ProjectRepository
    lateinit var mockRecommendationService: RecommendationService
    lateinit var mockCurrentTimeProvider: CurrentTimeProvider
    lateinit var pairingService: PairingService

    lateinit var project: Project

    val currentTime = Timestamp(1456364985548L)
    val p1 = Person("John", 1L)
    val p2 = Person("Mary", 2L)
    val p3 = Person("Steve", 3L)
    val p4 = Person("Tammy", 4L)

    //non null issue with mockito
    //https://medium.com/@elye.project/befriending-kotlin-and-mockito-1c2e7b0ef791#.kp2b058x7
    fun <T> uninitialized(): T = null as T

    fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    init {
        before {
            mockPairingHistoryRepository = mock(PairingHistoryRepository::class.java)
            mockProjectRepository = mock(ProjectRepository::class.java)
            mockRecommendationService = mock(RecommendationService::class.java)
            mockCurrentTimeProvider = mock(CurrentTimeProvider::class.java)
            pairingService = PairingService(mockPairingHistoryRepository, mockProjectRepository, mockRecommendationService, mockCurrentTimeProvider)
            `when`(mockCurrentTimeProvider.getCurrentTime()).thenReturn(currentTime)

            project = Project("One", "onepass")
        }

        describe("#savePairing") {
            test("creates a pairing history and persists it") {
                val pairingBoard = PairingBoard("The Pairing Board", mutableListOf(p1, p2), 1L)
                project.pairingBoards = mutableListOf(pairingBoard)

                val expectedPairingHistory = PairingHistory(project, listOf(p1, p2), currentTime, "The Pairing Board")

                `when`(mockProjectRepository.findOne(anyLong())).thenReturn(project)
                `when`(mockPairingHistoryRepository.save(expectedPairingHistory)).thenReturn(expectedPairingHistory)

                val result = pairingService.savePairing(7L)

                assertEquals(listOf(expectedPairingHistory), result)

                verify<ProjectRepository>(mockProjectRepository).findOne(7L)
                verify<PairingHistoryRepository>(mockPairingHistoryRepository).save(eq(expectedPairingHistory))
                verify<CurrentTimeProvider>(mockCurrentTimeProvider, times(1)).getCurrentTime()
            }

            test("creates multiple pairing histories with different pairing board names when there are more than one pairing boards") {
                val pairingBoard1 = PairingBoard("The Pairing Board", mutableListOf(p1, p2), 1L)
                val pairingBoard2 = PairingBoard("The Second Pairing Board", mutableListOf(p3, p4), 2L)

                project.pairingBoards = mutableListOf(pairingBoard1, pairingBoard2)

                val expectedPairingHistory1 = PairingHistory(project, listOf(p1, p2), currentTime, "The Pairing Board")
                val expectedPairingHistory2 = PairingHistory(project, listOf(p3, p4), currentTime, "The Second Pairing Board")

                `when`(mockProjectRepository.findOne(anyLong())).thenReturn(project)
                `when`(mockPairingHistoryRepository.save(expectedPairingHistory1)).thenReturn(expectedPairingHistory1)
                `when`(mockPairingHistoryRepository.save(expectedPairingHistory2)).thenReturn(expectedPairingHistory2)

                val result = pairingService.savePairing(7L)

                assertEquals(listOf(expectedPairingHistory1, expectedPairingHistory2), result)

                verify(mockProjectRepository).findOne(7L)
                verify(mockPairingHistoryRepository).save(eq(expectedPairingHistory1))
                verify(mockPairingHistoryRepository).save(eq(expectedPairingHistory2))
                verify(mockCurrentTimeProvider, times(1)).getCurrentTime()
            }

            test("creates multiple pairing histories with same pairing board name when a pairing board has more than two people") {
                val pairingBoard = PairingBoard("The Pairing Board", mutableListOf(p1, p2, p3), 1L)

                project.pairingBoards = mutableListOf(pairingBoard)

                val expectedPairingHistory = PairingHistory(project, listOf(p1, p2, p3), currentTime, "The Pairing Board")

                `when`(mockProjectRepository.findOne(anyLong())).thenReturn(project)
                `when`(mockPairingHistoryRepository.save(expectedPairingHistory)).thenReturn(expectedPairingHistory)

                val result = pairingService.savePairing(7L)

                assertEquals(listOf(expectedPairingHistory), result)

                verify(mockProjectRepository).findOne(7L)
                verify(mockPairingHistoryRepository).save(eq(expectedPairingHistory))
                verify(mockCurrentTimeProvider, times(1)).getCurrentTime()
            }

            test("creates a pairing history when there is only one person in a pairing board") {
                val pairingBoard = PairingBoard("The Pairing Board", mutableListOf(p1), 1L)

                project.pairingBoards = mutableListOf(pairingBoard)

                val expectedPairingHistory = PairingHistory(project, listOf(p1), currentTime, "The Pairing Board")

                `when`(mockProjectRepository.findOne(anyLong())).thenReturn(project)
                `when`(mockPairingHistoryRepository.save(expectedPairingHistory)).thenReturn(expectedPairingHistory)

                val result = pairingService.savePairing(7L)

                assertEquals(listOf(expectedPairingHistory), result)

                verify(mockProjectRepository).findOne(7L)
                verify(mockPairingHistoryRepository).save(eq(expectedPairingHistory))
                verify(mockCurrentTimeProvider, times(1)).getCurrentTime()
            }

            test("does not create a pairing history when there is no one in a pairing board") {
                val pairingBoard = PairingBoard(name = "The Pairing Board", id = 1L)

                project.pairingBoards = mutableListOf(pairingBoard)

                `when`(mockProjectRepository.findOne(anyLong())).thenReturn(project)

                val result = pairingService.savePairing(7L)
                assertEquals(emptyList(), result)

                verify(mockProjectRepository).findOne(7L)
                verifyZeroInteractions(mockPairingHistoryRepository)
                verify(mockCurrentTimeProvider, times(1)).getCurrentTime()
            }
        }

        describe("#getRecommendation") {
            test("gets the project and its pairing history and calls the recommendation service") {
                val pairingHistories = listOf(PairingHistory())

                `when`(mockProjectRepository.findOne(anyLong())).thenReturn(project)
                `when`(mockPairingHistoryRepository.findByProject(any())).thenReturn(pairingHistories)

                pairingService.getRecommendation(77L)

                verify(mockProjectRepository).findOne(77L)
                verify(mockPairingHistoryRepository).findByProject(project)
                verify(mockRecommendationService).get(project, pairingHistories)
            }

            test("persists the result from the recommendation service and returns the project") {
                val pairingHistories = listOf(PairingHistory())

                val recommendedProject = Project("One", "onepass")

                `when`(mockProjectRepository.findOne(anyLong())).thenReturn(project)
                `when`(mockPairingHistoryRepository.findByProject(any())).thenReturn(pairingHistories)
                `when`(mockRecommendationService.get(any(Project::class.java), anyListOf(PairingHistory::class.java))).thenReturn(recommendedProject)

                val returnedProject = pairingService.getRecommendation(77L)

                assertEquals(recommendedProject, returnedProject)

                verify(mockProjectRepository).save(recommendedProject)
            }
        }

        test("#getSortedPairingHistory gets the pairing histories for a project sorted by most recent pairing history") {
            val pairingHistories = listOf(
                    PairingHistory(project, timestamp = Timestamp(10), pairingBoardName = "Pairing Board"),
                    PairingHistory(project, timestamp = Timestamp(50), pairingBoardName = "Pairing Board 2"))

            `when`(mockProjectRepository.findOne(anyLong())).thenReturn(project)
            `when`(mockPairingHistoryRepository.findByProjectOrderByTimestampDesc(any())).thenReturn(pairingHistories)

            val result = pairingService.getSortedPairingHistory(7L)

            assertEquals(pairingHistories, result)

            verify(mockProjectRepository).findOne(7L)
            verify(mockPairingHistoryRepository).findByProjectOrderByTimestampDesc(project)
        }
    }
}