package com.parrit.services

import com.parrit.entities.PairingBoard
import com.parrit.entities.PairingHistory
import com.parrit.entities.Person
import com.parrit.entities.Project
import com.parrit.utilities.CurrentTimeProvider
import io.damo.kspec.Spec
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.sql.Timestamp
import kotlin.test.assertEquals


class RecommendationServiceTest : Spec() {

    lateinit var currentTimeProvider: CurrentTimeProvider
    lateinit var recommendationService: RecommendationService
    val today = 100

    lateinit var project: Project
    lateinit var pairingBoard1: PairingBoard
    lateinit var pairingBoard2: PairingBoard
    lateinit var pairingBoard3: PairingBoard

    val p1 = Person("Alpha", 1L)
    val p2 = Person("Bravo", 2L)
    val p3 = Person("Charlie", 3L)
    val p4 = Person("Delta", 4L)
    val p5 = Person("Epsilon", 5L)
    val p6 = Person("Foxtrot", 6L)

    init {
        before {
            currentTimeProvider = mock(CurrentTimeProvider::class.java)
            recommendationService = RecommendationService(currentTimeProvider)
            `when`(currentTimeProvider.getCurrentTime()).thenReturn(Timestamp(today.toLong()))

            project = Project("One", "onepass")
            pairingBoard1 = PairingBoard(name = "One", id = 1L)
            pairingBoard2 = PairingBoard(name = "Two", id = 2L)
            pairingBoard3 = PairingBoard(name = "Three", id = 3L)
        }

        describe("#get") {
            test("returns same project if no floating people") {
                val actualProject = recommendationService.get(project, emptyList())

                val expectedProject = Project("One", "onepass")

                assertEquals(expectedProject, actualProject)
            }

            test("moves floating person into pairing board if there's only one pairing board and no one in the pairing board") {
                project.people.add(p1)
                project.pairingBoards = mutableListOf(pairingBoard1)

                val actualProject = recommendationService.get(project, emptyList())

                val expectedProject = Project("One", "onepass", mutableListOf(PairingBoard("One", mutableListOf(p1), 1L)))

                assertEquals(expectedProject, actualProject)
            }

            test("moves floating person into empty space if all other spaces have at least two people in them") {
                project.people.add(p1)
                pairingBoard1.people.addAll(listOf(p2, p3))
                pairingBoard2.people.addAll(listOf(p4, p5, p6))
                project.pairingBoards = mutableListOf(pairingBoard1, pairingBoard2, pairingBoard3)

                val actualProject = recommendationService.get(project, emptyList())

                val expectedProject = Project("One", "onepass",
                        mutableListOf(PairingBoard("One", mutableListOf(p2, p3), 1L),
                                PairingBoard("Two", mutableListOf(p4, p5, p6), 2L),
                                PairingBoard("Three", mutableListOf(p1), 3L)))

                assertEquals(expectedProject, actualProject)
            }
        }

        describe("#get pairs floating") {
            test("person with a less recently paired person when given a choice between two viable pairing boards") {
                project.people.add(p1)
                pairingBoard1.people.add(p2)
                pairingBoard2.people.add(p3)
                project.pairingBoards = mutableListOf(pairingBoard1, pairingBoard2)
                val p1p2 = PairingHistory(project, listOf(p1, p2), daysAgo(1), "The Pairing Board")
                val p3p1 = PairingHistory(project, listOf(p3, p1), daysAgo(2), "The Second Pairing Board")

                val actualProject = recommendationService.get(project, listOf(p1p2, p3p1))

                val expectedProject = Project("One", "onepass",
                        mutableListOf(PairingBoard("One", mutableListOf(p2), 1L),
                                PairingBoard("Two", mutableListOf(p3, p1), 2L)))

                assertEquals(expectedProject, actualProject)
            }

            test("two people with two less recently paired people when both less recently paired with the same person") {
                project.people.addAll(listOf(p1, p2))
                pairingBoard1.people.add(p3)
                pairingBoard2.people.add(p4)
                project.pairingBoards = mutableListOf(pairingBoard1, pairingBoard2)
                val p1p3 = PairingHistory(project, listOf(p1, p3), daysAgo(1), "The Pairing Board")
                val p4p1 = PairingHistory(project, listOf(p4, p1), daysAgo(3), "The Second Pairing Board")
                val p2p3 = PairingHistory(project, listOf(p2, p3), daysAgo(2), "The Third Pairing Board")
                val p2p4 = PairingHistory(project, listOf(p2, p4), daysAgo(3), "The Fourth Pairing Board")

                val actualProject = recommendationService.get(project, listOf(p1p3, p4p1, p2p3, p2p4))

                val expectedProject = Project("One", "onepass",
                        mutableListOf(PairingBoard("One", mutableListOf(p3, p2), 1L),
                                PairingBoard("Two", mutableListOf(p4, p1), 2L)))

                assertEquals(expectedProject, actualProject)
            }

            test("people with pairs that leave good choices for others when best choice is not the obvious one") {
                project.people.addAll(listOf(p1, p2))
                pairingBoard1.people.add(p3)
                pairingBoard2.people.add(p4)
                project.pairingBoards = mutableListOf(pairingBoard1, pairingBoard2)
                val p1p3 = PairingHistory(project, listOf(p1, p3), daysAgo(25), "The Pairing Board")
                val p4p1 = PairingHistory(project, listOf(p4, p1), daysAgo(30), "The Second Pairing Board")
                val p2p3 = PairingHistory(project, listOf(p2, p3), daysAgo(20), "The Third Pairing Board")
                val p2p4 = PairingHistory(project, listOf(p2, p4), daysAgo(35), "The Fourth Pairing Board")

                val actualProject = recommendationService.get(project, listOf(p1p3, p4p1, p2p3, p2p4))

                val expectedProject = Project("One", "onepass",
                        mutableListOf(PairingBoard("One", mutableListOf(p3, p1), 1L),
                                PairingBoard("Two", mutableListOf(p4, p2), 2L)))

                assertEquals(expectedProject, actualProject)
            }

            test("people with pairs when there are more floating people than available") {
                project.people.addAll(listOf(p1, p2))
                pairingBoard1.people.add(p3)
                project.pairingBoards = mutableListOf(pairingBoard1, pairingBoard2)
                val p1p2 = PairingHistory(project, listOf(p1, p2), daysAgo(15), "The Pairing Board")
                val p1p3 = PairingHistory(project, listOf(p1, p3), daysAgo(25), "The Pairing Board")

                val actualProject = recommendationService.get(project, listOf(p1p2, p1p3))

                val expectedProject = Project("One", "onepass",
                        mutableListOf(PairingBoard("One", mutableListOf(p3, p2), 1L),
                                PairingBoard("Two", mutableListOf(p1), 2L)))

                assertEquals(expectedProject, actualProject)
            }

            test("people with with each other efficiently overall when there are more floating people than available") {
                project.people.addAll(listOf(p1, p2, p3))
                pairingBoard1.people.add(p4)
                project.pairingBoards = mutableListOf(pairingBoard1, pairingBoard2)
                val p4p1 = PairingHistory(project, listOf(p4, p1), daysAgo(30), "The Pairing Board")
                val p4p2 = PairingHistory(project, listOf(p4, p2), daysAgo(20), "The Pairing Board")
                val p1p2 = PairingHistory(project, listOf(p1, p2), daysAgo(10), "The Pairing Board")

                val actualProject = recommendationService.get(project, listOf(p4p1, p4p2, p1p2))

                val expectedProject = Project("One", "onepass",
                        mutableListOf(PairingBoard("One", mutableListOf(p4, p1), 1L),
                                PairingBoard("Two", mutableListOf(p2, p3), 2L)))

                assertEquals(expectedProject, actualProject)
            }

            test("people in a newly created pairing board if there are not enough pairing boards left") {
                project.people.addAll(listOf(p1, p2, p3, p4, p5))
                project.pairingBoards = mutableListOf(pairingBoard1)

                val actualProject = recommendationService.get(project, emptyList())

                val expectedProject = Project("One", "onepass",
                        mutableListOf(PairingBoard("One", mutableListOf(p1, p5), 1L),
                                PairingBoard("New Pairing Board", mutableListOf(p2, p4)),
                                PairingBoard("New Pairing Board", mutableListOf(p3))))

                assertEquals(expectedProject, actualProject)
            }

            test("people with pairs least recent paired with a Trair pairing history") {
                project.people.addAll(listOf(p1, p2, p3))
                pairingBoard1.people.add(p4)
                project.pairingBoards = mutableListOf(pairingBoard1)
                val p1p2p3 = PairingHistory(project, listOf(p1, p2, p3), daysAgo(2), "The Pairing Board")

                val actualProject = recommendationService.get(project, listOf(p1p2p3))

                val expectedProject = Project("One", "onepass",
                        mutableListOf(PairingBoard("One", mutableListOf(p4, p1), 1L),
                                PairingBoard("New Pairing Board", mutableListOf(p2, p3))))

                assertEquals(expectedProject, actualProject)
            }
        }
    }

    fun daysAgo(days: Int): Timestamp = Timestamp((today - days).toLong())
}