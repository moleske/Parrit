package com.parrit.services

import com.parrit.entities.PairingBoard
import com.parrit.entities.PairingHistory
import com.parrit.entities.Person
import com.parrit.entities.Project
import com.parrit.support.MockitoTestBase
import com.parrit.utilities.CurrentTimeProvider
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import java.sql.Timestamp

class RecommendationServiceTest : MockitoTestBase() {

    @Mock
    lateinit var currentTimeProvider: CurrentTimeProvider

    lateinit var recommendationService: RecommendationService

    lateinit var project: Project
    lateinit var pairingBoard1: PairingBoard
    lateinit var pairingBoard2: PairingBoard
    lateinit var pairingBoard3: PairingBoard
    lateinit var exemptPairingBoard: PairingBoard
    lateinit var p1: Person
    lateinit var p2: Person
    lateinit var p3: Person
    lateinit var p4: Person
    lateinit var p5: Person
    lateinit var p6: Person
    lateinit var pairingHistories: MutableList<PairingHistory>

    var today = 100
    private fun daysAgo(days: Int): Timestamp {
        assert(days <= today)
        return Timestamp((today - days).toLong())
    }

    @Before
    fun setup() {
        recommendationService = RecommendationService(currentTimeProvider)

        project = Project("One", "onepass")

        pairingBoard1 = PairingBoard("One", mutableListOf())
        pairingBoard1.id = 1L
        pairingBoard2 = PairingBoard("Two", mutableListOf())
        pairingBoard2.id = 2L
        pairingBoard3 = PairingBoard("Three", mutableListOf())
        pairingBoard3.id = 3L
        exemptPairingBoard = PairingBoard("Exempt", mutableListOf())
        exemptPairingBoard.id = 4L
        exemptPairingBoard.isExempt = true

        p1 = Person(id = 1L, name = "Alpha")
        p2 = Person(id = 2L, name = "Bravo")
        p3 = Person(id = 3L, name = "Charlie")
        p4 = Person(id = 4L, name = "Delta")
        p5 = Person(id = 5L, name = "Epsilon")
        p6 = Person(id = 6L, name = "Foxtrot")

        pairingHistories = mutableListOf()

        `when`(currentTimeProvider.currentTime).thenReturn(Timestamp(today.toLong()))
    }

    @Test
    fun get_returnsTheSameProject_ifThereAreNotFloatingPeople() {
        val returnedProject = recommendationService.get(project, pairingHistories)

        assertThat(returnedProject, equalTo(project))
    }

    @Test
    fun get_movesAFloatingPersonIntoAPairingBoard_ifThereIsOnlyOnePairingBoardAndNoOneInThePairingBoard() {
        project.people.add(p1)
        project.pairingBoards.add(pairingBoard1)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedPairingBoard = PairingBoard("One", mutableListOf(p1))
        expectedPairingBoard.id = 1L

        val expectedProject = Project("One", "onepass", mutableListOf(expectedPairingBoard))

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_movesAFloatingPersonIntoAnEmptySpace_ifAllOtherSpacesHaveAtLeastTwoPeopleInThem() {
        project.people.add(p1)

        pairingBoard1.people.add(p2)
        pairingBoard1.people.add(p3)
        project.pairingBoards.add(pairingBoard1)

        pairingBoard2.people.add(p4)
        pairingBoard2.people.add(p5)
        pairingBoard2.people.add(p6)
        project.pairingBoards.add(pairingBoard2)

        project.pairingBoards.add(pairingBoard3)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedProject = Project("One", "onepass")

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p2, p3))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        val pairingBoard2Expected = PairingBoard("Two", mutableListOf(p4, p5, p6))
        pairingBoard2Expected.id = 2L
        expectedProject.pairingBoards.add(pairingBoard2Expected)

        val pairingBoard3Expected = PairingBoard("Three", mutableListOf(p1))
        pairingBoard3Expected.id = 3L
        expectedProject.pairingBoards.add(pairingBoard3Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_pairsAFloatingPersonWithALessRecentlyPairedPerson_whenGivenAChoiceBetweenTwoViablePairingBoards() {
        project.people.add(p1)

        pairingBoard1.people.add(p2)
        project.pairingBoards.add(pairingBoard1)

        pairingBoard2.people.add(p3)
        project.pairingBoards.add(pairingBoard2)

        val p1p2 = PairingHistory(project, mutableListOf(p1, p2), daysAgo(1), "The Pairing Board")
        pairingHistories.add(p1p2)

        val p3p1 = PairingHistory(project, mutableListOf(p3, p1), daysAgo(2), "The Second Pairing Board")
        pairingHistories.add(p3p1)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedProject = Project("One", "onepass")

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p2))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        val pairingBoard2Expected = PairingBoard("Two", mutableListOf(p3, p1))
        pairingBoard2Expected.id = 2L
        expectedProject.pairingBoards.add(pairingBoard2Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_pairsTwoFloatingPeopleWithTwoLessRecentlyPairedPeople_whenBothLessRecentlyPairedWithTheSamePerson() {
        project.people.add(p1)
        project.people.add(p2)

        pairingBoard1.people.add(p3)
        project.pairingBoards.add(pairingBoard1)

        pairingBoard2.people.add(p4)
        project.pairingBoards.add(pairingBoard2)

        val p1p3 = PairingHistory(project, mutableListOf(p1, p3), daysAgo(1), "The Pairing Board")
        pairingHistories.add(p1p3)

        val p4p1 = PairingHistory(project, mutableListOf(p4, p1), daysAgo(3), "The Second Pairing Board")
        pairingHistories.add(p4p1)

        val p2p3 = PairingHistory(project, mutableListOf(p2, p3), daysAgo(2), "The Third Pairing Board")
        pairingHistories.add(p2p3)

        val p2p4 = PairingHistory(project, mutableListOf(p2, p4), daysAgo(3), "The Fourth Pairing Board")
        pairingHistories.add(p2p4)

        val returnedProject = recommendationService.get(project, pairingHistories)
        val expectedProject = Project("One", "onepass")

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p3, p2))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        val pairingBoard2Expected = PairingBoard("Two", mutableListOf(p4, p1))
        pairingBoard2Expected.id = 2L
        expectedProject.pairingBoards.add(pairingBoard2Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_pairsFloatingPeopleWithPairsThatLeaveGoodChoicesForOthers_whenTheBestChoiceIsNotTheObviousOne() {
        project.people.add(p1)
        project.people.add(p2)

        pairingBoard1.people.add(p3)
        project.pairingBoards.add(pairingBoard1)

        pairingBoard2.people.add(p4)
        project.pairingBoards.add(pairingBoard2)

        val p1p3 = PairingHistory(project, mutableListOf(p1, p3), daysAgo(25), "The Pairing Board")
        pairingHistories.add(p1p3)

        val p4p1 = PairingHistory(project, mutableListOf(p4, p1), daysAgo(30), "The Second Pairing Board")
        pairingHistories.add(p4p1)

        val p2p3 = PairingHistory(project, mutableListOf(p2, p3), daysAgo(20), "The Third Pairing Board")
        pairingHistories.add(p2p3)

        val p2p4 = PairingHistory(project, mutableListOf(p2, p4), daysAgo(35), "The Fourth Pairing Board")
        pairingHistories.add(p2p4)

        val returnedProject = recommendationService.get(project, pairingHistories)
        val expectedProject = Project("One", "onepass")

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p3, p1))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        val pairingBoard2Expected = PairingBoard("Two", mutableListOf(p4, p2))
        pairingBoard2Expected.id = 2L
        expectedProject.pairingBoards.add(pairingBoard2Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_pairsFloatingPeopleWithPairs_whenThereAreMoreFloatingPeopleThanAvailable() {
        project.people.add(p1)
        project.people.add(p2)

        pairingBoard1.people.add(p3)
        project.pairingBoards.add(pairingBoard1)

        project.pairingBoards.add(pairingBoard2)

        val p1p2 = PairingHistory(project, mutableListOf(p1, p2), daysAgo(15), "The Pairing Board")
        pairingHistories.add(p1p2)

        val p1p3 = PairingHistory(project, mutableListOf(p1, p3), daysAgo(25), "The Pairing Board")
        pairingHistories.add(p1p3)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedProject = Project("One", "onepass")

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p3, p2))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        val pairingBoard2Expected = PairingBoard("Two", mutableListOf(p1))
        pairingBoard2Expected.id = 2L
        expectedProject.pairingBoards.add(pairingBoard2Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_pairsFloatingPeopleWithEachOtherEfficientlyOverall_whenThereAreMoreFloatingPeopleThanAvailable() {
        project.people.add(p1)
        project.people.add(p2)
        project.people.add(p3)

        pairingBoard1.people.add(p4)
        project.pairingBoards.add(pairingBoard1)

        project.pairingBoards.add(pairingBoard2)

        val p4p1 = PairingHistory(project, mutableListOf(p4, p1), daysAgo(30), "The Pairing Board")
        pairingHistories.add(p4p1)

        val p4p2 = PairingHistory(project, mutableListOf(p4, p2), daysAgo(20), "The Pairing Board")
        pairingHistories.add(p4p2)

        val p1p2 = PairingHistory(project, mutableListOf(p1, p2), daysAgo(10), "The Pairing Board")
        pairingHistories.add(p1p2)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedProject = Project("One", "onepass")

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p4, p1))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        val pairingBoard2Expected = PairingBoard("Two", mutableListOf(p2, p3))
        pairingBoard2Expected.id = 2L
        expectedProject.pairingBoards.add(pairingBoard2Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_pairsFloatingPeopleInANewlyCreatedPairingBoard_ifThereAreNotEnoughPairingBoardsLeft() {
        project.people.add(p1)
        project.people.add(p2)
        project.people.add(p3)
        project.people.add(p4)
        project.people.add(p5)

        project.pairingBoards.add(pairingBoard1)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedProject = Project("One", "onepass")

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p1, p5))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        val pairingBoard2Expected = PairingBoard("New Pairing Board", mutableListOf(p2, p4)) //Null Id
        expectedProject.pairingBoards.add(pairingBoard2Expected)

        val pairingBoard3Expected = PairingBoard("New Pairing Board", mutableListOf(p3)) //Null Id
        expectedProject.pairingBoards.add(pairingBoard3Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_pairsFloatingPeopleWithPairsLeastRecentPaired_withATrairPairingHistory() {
        project.people.add(p1)
        project.people.add(p2)
        project.people.add(p3)

        pairingBoard1.people.add(p4)
        project.pairingBoards.add(pairingBoard1)

        val p1p2p3 = PairingHistory(project, mutableListOf(p1, p2, p3), daysAgo(2), "The Pairing Board")
        pairingHistories.add(p1p2p3)

        val p4solo = PairingHistory(project, listOf(p4), daysAgo(2), "The Pairing Board")
        pairingHistories.add(p4solo)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedProject = Project("One", "onepass")

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p4, p1))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        val pairingBoard2Expected = PairingBoard("New Pairing Board", mutableListOf(p2, p3)) //Null Id
        expectedProject.pairingBoards.add(pairingBoard2Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_doesNotConsiderPeople_whenTheyAreInExemptPairingBoards() {
        project.people.add(p1)

        exemptPairingBoard.people.add(p2)

        project.pairingBoards.add(exemptPairingBoard)
        project.pairingBoards.add(pairingBoard1)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedProject = Project("One", "onepass")

        val exemptBoardExpected = PairingBoard("Exempt", mutableListOf(p2))
        exemptBoardExpected.id = 4L
        expectedProject.pairingBoards.add(exemptBoardExpected)

        val pairingBoard1Expected = PairingBoard("One", mutableListOf(p1))
        pairingBoard1Expected.id = 1L
        expectedProject.pairingBoards.add(pairingBoard1Expected)

        assertThat(returnedProject, equalTo(expectedProject))
    }

    @Test
    fun get_doesNotUseExemptPairingBoardsToPairFloatingPeople() {
        project.people.add(p1)
        project.people.add(p2)
        project.pairingBoards.add(exemptPairingBoard)

        val returnedProject = recommendationService.get(project, pairingHistories)

        val expectedProject = Project("One", "onepass")

        val pairingBoardExpectedNew = PairingBoard("New Pairing Board", mutableListOf(p1, p2))
        pairingBoardExpectedNew.id = 0L

        val pairingBoardExpectedExempt = PairingBoard("Exempt", mutableListOf())
        pairingBoardExpectedExempt.id = 4L

        expectedProject.pairingBoards.add(pairingBoardExpectedExempt)
        expectedProject.pairingBoards.add(pairingBoardExpectedNew)

        assertThat(returnedProject, equalTo(expectedProject))
    }
}