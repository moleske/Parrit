package com.parrit.controllers

import com.parrit.DTOs.ProjectDTO
import com.parrit.entities.PairingBoard
import com.parrit.entities.Person
import com.parrit.entities.Project
import com.parrit.repositories.ProjectRepository
import com.parrit.support.ControllerTestBase
import com.parrit.transformers.ProjectTransformer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.InjectMocks
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.util.NestedServletException

class ProjectControllerTest : ControllerTestBase() {

    @Mock
    lateinit var mockProjectRepository: ProjectRepository

    @Autowired
    @InjectMocks
    lateinit var projectController: ProjectController

    lateinit var persistedProject: Project
    lateinit var updatedProject: Project
    lateinit var updatedProjectDTO: ProjectDTO
    lateinit var persistedProjectString: String
    lateinit var updatedProjectDTOString: String

    @get:Rule
    var thrown: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {
        persistedProject = Project("Henry", "henrypass", mutableListOf(), mutableListOf(), 1L)
        persistedProjectString = "{\"id\":1,\"name\":\"Henry\",\"password\":\"henrypass\",\"pairingBoards\":[],\"people\":[]}"

        updatedProjectDTO = ProjectTransformer.transform(persistedProject)
        updatedProjectDTOString = "{\"id\":1,\"name\":\"Bob\",\"pairingBoards\":[],\"people\":[]}"

        updatedProject = Project("Henry", "henrypass", mutableListOf(), mutableListOf(), 1L)
    }

    //*********************//
    //******  Views  ******//
    //*********************//

    @Test
    fun getDashboard_returnsDashboardView() {
        mvc.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(view().name("dashboard"))
    }

    @Test
    fun getProject_returnsResultFromRepository() {
        `when`(mockProjectRepository.findByName("anyProjectName")).thenReturn(persistedProject)

        mvc.perform(get("/anyProjectName"))
                .andExpect(status().isOk)
                .andExpect(view().name("project"))
                .andExpect(model().attribute("project", updatedProjectDTO))

        verify(mockProjectRepository, never()).save(any(Project::class.java))
        verify(mockProjectRepository).findByName("anyProjectName")
    }

    //********************//
    //******  APIs  ******//
    //********************//

    @Test
    fun createProject_savesTheNewProject() {
        `when`(mockProjectRepository.save(any(Project::class.java))).thenReturn(persistedProject)

        mvc.perform(post("/api/project/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"bob\",\"password\":\"bobpass\"}"))
                .andExpect(status().isOk)

        val newProject = Project("bob", "da7655b5bf67039c3e76a99d8e6fb6969370bbc0fa440cae699cf1a3e2f1e0a1", mutableListOf(), mutableListOf())

        newProject.pairingBoards.add(PairingBoard("COCKATOO", mutableListOf()))
        newProject.pairingBoards.add(PairingBoard("MACAW", mutableListOf()))
        newProject.pairingBoards.add(PairingBoard("LOVEBIRD", mutableListOf()))
        newProject.pairingBoards.add(PairingBoard("PARAKEET", mutableListOf()))
        newProject.pairingBoards.add(PairingBoard("DESIGN", mutableListOf()))
        newProject.pairingBoards.add(PairingBoard("OUT OF OFFICE", mutableListOf()))

        verify(mockProjectRepository).save(Matchers.eq(newProject))
    }

    @Test
    fun createProject_throwsAnException_whenThePasswordIsEmpty() {
        thrown.expect(NestedServletException::class.java)

        mvc.perform(post("/api/project/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"bob\",\"password\":\"\"}"))
    }

    @Test
    fun saveProject_persistsTheProjectWithChanges_andReturnsTheResult() {
        updatedProject.name = "Bob"

        `when`(mockProjectRepository.findOne(Matchers.anyLong())).thenReturn(persistedProject)
        `when`(mockProjectRepository.save(any(Project::class.java))).thenReturn(updatedProject)

        val mvcResult = mvc.perform(post("/api/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedProjectDTOString))
                .andExpect(status().isOk)
                .andReturn()

        val returnedProject = mvcResult.response.contentAsString
        assertThat(returnedProject, equalTo(updatedProjectDTOString))

        verify(mockProjectRepository).findOne(1L)
        verify(mockProjectRepository).save(Matchers.eq(updatedProject))
    }

    @Test
    fun addPerson_createsAPersonWithThePassedInName_andReturnsTheUpdatedProject() {
        val newPerson = Person("Steve")
        updatedProject.people.add(newPerson)

        `when`(mockProjectRepository.findOne(Matchers.anyLong())).thenReturn(persistedProject)
        `when`(mockProjectRepository.save(any(Project::class.java))).thenReturn(updatedProject)

        val mvcResult = mvc.perform(post("/api/project/2/addPerson")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Steve\"}"))
                .andExpect(status().isOk)
                .andReturn()

        val returnedProject = mvcResult.response.contentAsString
        assertThat(returnedProject, equalTo("{\"id\":1,\"name\":\"Henry\",\"pairingBoards\":[],\"people\":[{\"id\":0,\"name\":\"Steve\"}]}"))

        verify(mockProjectRepository).findOne(2L)
        verify(mockProjectRepository).save(updatedProject)
    }
}
