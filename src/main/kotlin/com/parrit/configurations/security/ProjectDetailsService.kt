package com.parrit.configurations.security

import com.parrit.repositories.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class ProjectDetailsService @Autowired
constructor(private val projectRepository: ProjectRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(name: String): UserDetails {
        val project = projectRepository.findByName(name) ?: throw UsernameNotFoundException("Username <$name> was not found")

        return User(project.name, project.password, emptyList())
    }
}
