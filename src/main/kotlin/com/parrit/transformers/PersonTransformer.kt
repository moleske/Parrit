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
        when {
            persons == null || persons.isEmpty() -> return emptyList()
            else -> return persons.map { transform(it) }
        }
    }

    fun reverse(personDTO: PersonDTO) = Person(id = personDTO.id, name = personDTO.name)

    fun reverse(personDTOs: List<PersonDTO>?): MutableList<Person> {
        when {
            personDTOs == null || personDTOs.isEmpty() -> return mutableListOf()
            else -> return personDTOs.map { reverse(it) }.toMutableList()
        }
    }

}
