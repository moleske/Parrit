package com.parrit.controllers

import com.parrit.entities.PairingBoard
import com.parrit.entities.PairingHistory
import com.parrit.entities.Project
import com.parrit.services.PairingService
import com.parrit.support.ControllerTestBase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.sql.Timestamp
import java.util.*

class PairingControllerTest : ControllerTestBase() {

    @Mock
    lateinit var mockPairingService: PairingService

    @Autowired
    @InjectMocks
    lateinit var pairingController: PairingController

    lateinit var exampleProject: Project
    lateinit var exampleProjectString: String

    @Before
    fun setUp() {
        val pairingBoard = PairingBoard("Super Pairing Board", mutableListOf())
        pairingBoard.id = 1L

        exampleProject = Project("Nancy", "nancypass", mutableListOf(pairingBoard), mutableListOf(), 2L)

        val pairingBoardString = "{\"id\":1,\"people\":[],\"exempt\":false,\"name\":\"Super Pairing Board\"}"
        exampleProjectString = "{\"id\":2,\"name\":\"Nancy\",\"pairingBoards\":[$pairingBoardString],\"people\":[]}"
    }

    //********************//
    //******  APIs  ******//
    //********************//

    @Test
    fun savePairing_passesTheProjectToThePairingHistoryService_andReturnsTheResultingPairingHistories() {
        val pairingHistory1 = PairingHistory(exampleProject, mutableListOf(), Timestamp(120000), "Pairing Board 1")
        val pairingHistory2 = PairingHistory(exampleProject, mutableListOf(), Timestamp(60000), "Pairing Board 2")
        val pairingHistory3 = PairingHistory(exampleProject, mutableListOf(), Timestamp(60000), "Pairing Board 3")

        `when`(mockPairingService.savePairing(Matchers.anyLong())).thenReturn(Arrays.asList(pairingHistory1, pairingHistory2, pairingHistory3))

        val mvcResult = mvc.perform(post("/api/project/42/pairing")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()

        val expectedResult = "[" +
                "{\"pairingTime\":\"1970-01-01T00:02:00.000+0000\",\"people\":[],\"pairingBoardName\":\"Pairing Board 1\"}," +
                "{\"pairingTime\":\"1970-01-01T00:01:00.000+0000\",\"people\":[],\"pairingBoardName\":\"Pairing Board 2\"}," +
                "{\"pairingTime\":\"1970-01-01T00:01:00.000+0000\",\"people\":[],\"pairingBoardName\":\"Pairing Board 3\"}" +
                "]"

        val returnedProject = mvcResult.response.contentAsString
        assertThat(returnedProject, equalTo(expectedResult))

        verify(mockPairingService).savePairing(42)
    }

    @Test
    fun getRecommendation_passesTheProjectToThePairingService_andReturnsAModifiedProject() {
        `when`(mockPairingService.getRecommendation(Matchers.anyLong())).thenReturn(exampleProject)

        val mvcResult = mvc.perform(get("/api/project/42/pairing/recommend")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()

        val returnedProject = mvcResult.response.contentAsString
        assertThat(returnedProject, equalTo(exampleProjectString))

        verify(mockPairingService).getRecommendation(42)
    }

    @Test
    fun getPairingHistory_callsPairingHistoryService_andReturnsAMapOfTimestampsToListsOfPairHistories() {
        val pairingHistory1 = PairingHistory(exampleProject, mutableListOf(), Timestamp(120000), "Pairing Board 1")
        val pairingHistory2 = PairingHistory(exampleProject, mutableListOf(), Timestamp(60000), "Pairing Board 2")
        val pairingHistory3 = PairingHistory(exampleProject, mutableListOf(), Timestamp(60000), "Pairing Board 3")

        `when`(mockPairingService.getSortedPairingHistory(Matchers.anyLong())).thenReturn(Arrays.asList(pairingHistory1, pairingHistory2, pairingHistory3))

        val mvcResult = mvc.perform(get("/api/project/42/pairing/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()

        val expectedResult = "[" +
                "{\"pairingTime\":\"1970-01-01T00:02:00.000+0000\",\"people\":[],\"pairingBoardName\":\"Pairing Board 1\"}," +
                "{\"pairingTime\":\"1970-01-01T00:01:00.000+0000\",\"people\":[],\"pairingBoardName\":\"Pairing Board 2\"}," +
                "{\"pairingTime\":\"1970-01-01T00:01:00.000+0000\",\"people\":[],\"pairingBoardName\":\"Pairing Board 3\"}" +
                "]"

        val returnedProject = mvcResult.response.contentAsString
        assertThat(returnedProject, equalTo(expectedResult))

        verify(mockPairingService).getSortedPairingHistory(42)
    }
}
