package com.parrit.entities

import javax.persistence.*

@Entity
class PairingBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var name: String? = null

    @OneToMany(targetEntity = Person::class)
    @JoinColumn(name = "pairing_board_id")
    var people: List<Person>? = null

    constructor() {
    }

    constructor(name: String, people: List<Person>) {
        this.name = name
        this.people = people
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is PairingBoard) return false

        if (id != o.id) return false
        if (if (name != null) name != o.name else o.name != null) return false
        return if (people != null) people == o.people else o.people == null

    }

    override fun hashCode(): Int {
        var result = (id xor id.ushr(32)).toInt()
        result = 31 * result + if (name != null) name!!.hashCode() else 0
        result = 31 * result + if (people != null) people!!.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "PairingBoard{id=$id, name='$name', people=$people}"
    }
}
