package com.parrit.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
data class PairingHistory @JvmOverloads constructor(
        @ManyToOne(targetEntity = Project::class, fetch = FetchType.LAZY)
        @JoinColumn(name = "project_id")
        var project: Project? = null,

        @ManyToMany(targetEntity = Person::class, fetch = FetchType.LAZY)
        @JoinTable(inverseJoinColumns = arrayOf(JoinColumn(name = "person_id")))
        var people: List<Person> = emptyList(),

        var timestamp: Timestamp? = null,

        var pairingBoardName: String = "",

        @Id
        @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
        var id: Long = 0
)
