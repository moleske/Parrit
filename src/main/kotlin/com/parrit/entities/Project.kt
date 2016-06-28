package com.parrit.entities

import org.hibernate.validator.constraints.Length

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotNull
    @Length(min = 1, max = 36)
    @Column(unique = true)
    var name: String? = null

    @NotNull
    var password: String? = null

    @OneToMany(targetEntity = PairingBoard::class, cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
    @JoinColumn(name = "project_id")
    var pairingBoards: List<PairingBoard>? = null

    @OneToMany(targetEntity = Person::class, cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "project_id")
    var people: List<Person>? = null

    constructor() {
    }

    constructor(name: String, password: String, pairingBoards: List<PairingBoard>, people: List<Person>) {
        this.name = name
        this.password = password
        this.pairingBoards = pairingBoards
        this.people = people
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Project) return false

        if (id != o.id) return false
        if (if (name != null) name != o.name else o.name != null) return false
        if (if (password != null) password != o.password else o.password != null) return false
        if (if (pairingBoards != null) pairingBoards != o.pairingBoards else o.pairingBoards != null) return false
        return if (people != null) people == o.people else o.people == null

    }

    override fun hashCode(): Int {
        var result = (id xor id.ushr(32)).toInt()
        result = 31 * result + if (name != null) name!!.hashCode() else 0
        result = 31 * result + if (password != null) password!!.hashCode() else 0
        result = 31 * result + if (pairingBoards != null) pairingBoards!!.hashCode() else 0
        result = 31 * result + if (people != null) people!!.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "Workspace{id=$id, name='$name', password='$password', pairingBoards=$pairingBoards, people=$people}"
    }
}