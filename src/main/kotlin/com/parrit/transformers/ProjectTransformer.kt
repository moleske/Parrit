package com.parrit.transformers

import com.parrit.DTOs.ProjectDTO
import com.parrit.entities.Project

object ProjectTransformer {

    fun transform(project: Project): ProjectDTO {
        val projectDTO = ProjectDTO()
        projectDTO.id = project.id
        projectDTO.name = project.name
        projectDTO.people = PersonTransformer.transform(project.people)
        projectDTO.pairingBoards = PairingBoardTransformer.transform(project.pairingBoards)
        return projectDTO
    }

    fun merge(project: Project, projectDTO: ProjectDTO): Project {
        val mergedProject = Project()
        mergedProject.id = project.id
        mergedProject.name = projectDTO.name
        mergedProject.password = project.password
        mergedProject.people = PersonTransformer.reverse(projectDTO.people)
        mergedProject.pairingBoards = PairingBoardTransformer.reverse(projectDTO.pairingBoards)
        return mergedProject
    }
}
