package com.parrit.entities

import javax.persistence.*

@Entity
data class PairingBoard @JvmOverloads constructor(
        var name: String = "",

        @OneToMany(targetEntity = Person::class)
        @JoinColumn(name = "pairing_board_id")
        var people: List<Person> = emptyList(),

        @Id
        @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
        var id: Long = 0
)
