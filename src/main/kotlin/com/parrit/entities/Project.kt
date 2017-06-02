package com.parrit.entities

import org.hibernate.validator.constraints.Length
import javax.persistence.*

@Entity
data class Project
@JvmOverloads constructor(
        @Length(min = 1, max = 36)
        @Column(unique = true)
        var name: String = "",

        var password: String = "",

        @OneToMany(targetEntity = PairingBoard::class, cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
        @JoinColumn(name = "project_id")
        var pairingBoards: MutableList<PairingBoard> = mutableListOf(),

        @OneToMany(targetEntity = Person::class, cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "project_id")
        var people: MutableList<Person> = mutableListOf(),

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0
)