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
    var name: String? = null

    constructor() {
    }

    constructor(name: String) {
        this.name = name
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Person) return false

        if (id != o.id) return false
        return if (name != null) name == o.name else o.name == null

    }

    override fun hashCode(): Int {
        var result = (id xor id.ushr(32)).toInt()
        result = 31 * result + if (name != null) name!!.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "Person{id=$id, name='$name'}"
    }
}