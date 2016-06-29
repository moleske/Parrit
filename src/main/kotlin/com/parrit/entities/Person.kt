package com.parrit.entities

import org.hibernate.validator.constraints.Length

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotNull
    @Length(min = 1, max = 10)
    var name: String = ""

    constructor() {
    }

    constructor(name: String) {
        this.name = name
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Person

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int{
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String = "Person{id=$id, name='$name'}"
}