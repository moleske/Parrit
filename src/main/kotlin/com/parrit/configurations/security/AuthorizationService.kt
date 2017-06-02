package com.parrit.configurations.security

import com.parrit.DTOs.ProjectDTO
import com.parrit.entities.Project
import com.parrit.repositories.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class AuthorizationService @Autowired
constructor(private val projectRepository: ProjectRepository) {

    fun canAccessProject(user: User?, projectName: String) = user != null && user.username == projectName

    fun canAccessProject(user: User, project: Project) = canAccessProject(user, project.name)

    fun canAccessProject(user: User, projectId: Long) = canAccessProject(user, projectRepository.findOne(projectId))

    fun canAccessProject(user: User, projectDTO: ProjectDTO) = canAccessProject(user, projectDTO.name)
}