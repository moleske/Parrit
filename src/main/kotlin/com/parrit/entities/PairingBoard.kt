package com.parrit.entities

import javax.persistence.*

@Entity
class PairingBoard {

    var name: String = ""

    @OneToMany(targetEntity = Person::class)
    @JoinColumn(name = "pairing_board_id")
    var people: MutableList<Person> = mutableListOf()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var isExempt: Boolean = false // Set to true if floating people should NOT be moved into this space

    constructor()

    constructor(name: String, people: MutableList<Person>) {
        this.name = name
        this.people = people
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PairingBoard) return false

        val pairingBoard = other

        if (id != pairingBoard.id) return false
        if (name != pairingBoard.name) return false
        return people == pairingBoard.people

    }

    override fun hashCode(): Int {
        var result = (id xor id.ushr(32)).toInt()
        result = 31 * result + name.hashCode()
        result = 31 * result + people.hashCode()
        return result
    }

    override fun toString(): String {
        return "PairingBoard(name='$name', people=$people, id=$id, isExempt=$isExempt)"
    }

}
