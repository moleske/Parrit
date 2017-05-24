package com.parrit.transformers

import com.parrit.DTOs.PersonDTO
import com.parrit.entities.Person

object PersonTransformer {

    fun transform(person: Person): PersonDTO {
        val personDTO = PersonDTO()
        personDTO.id = person.id
        personDTO.name = person.name
        return personDTO
    }

    fun transform(persons: List<Person>?): List<PersonDTO> {
        if (persons == null || persons.isEmpty()) return emptyList()
        return persons.map { transform(it) }
    }

    fun reverse(personDTO: PersonDTO): Person {
        val person = Person()
        person.id = personDTO.id
        person.name = personDTO.name
        return person
    }

    fun reverse(personDTOs: List<PersonDTO>?): List<Person> {
        if (personDTOs == null || personDTOs.isEmpty()) return emptyList()
        return personDTOs.map { reverse(it) }
    }

}
