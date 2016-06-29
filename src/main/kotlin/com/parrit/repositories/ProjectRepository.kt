package com.parrit.repositories

import com.parrit.entities.Project
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : CrudRepository<Project, Long> {

    fun findByName(name: String): Project?
}
