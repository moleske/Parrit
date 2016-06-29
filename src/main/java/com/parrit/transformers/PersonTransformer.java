package com.parrit.transformers;

import com.parrit.DTOs.PersonDTO;
import com.parrit.entities.Person;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PersonTransformer {

    public static PersonDTO transform(Person person) {
        return new PersonDTO(person.getId(), person.getName());
    }

    public static List<PersonDTO> transform(List<Person> persons) {
        if(persons == null || persons.isEmpty()) return Collections.emptyList();
        return persons.stream()
                .map(PersonTransformer::transform)
                .collect(Collectors.toList());
    }

    public static Person reverse(PersonDTO personDTO) {
        Person person = new Person(personDTO.getName());
        person.setId(personDTO.getId());
        return person;
    }

    public static List<Person> reverse(List<PersonDTO> personDTOs) {
        if(personDTOs == null || personDTOs.isEmpty()) return Collections.emptyList();
        return personDTOs.stream()
            .map(PersonTransformer::reverse)
            .collect(Collectors.toList());
    }

}
