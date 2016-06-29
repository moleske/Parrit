package com.parrit.transformers;

import com.parrit.DTOs.PairingBoardDTO;
import com.parrit.DTOs.PersonDTO;
import com.parrit.DTOs.ProjectDTO;
import com.parrit.entities.PairingBoard;
import com.parrit.entities.Person;
import com.parrit.entities.Project;

import java.util.List;

public class ProjectTransformer {

    public static ProjectDTO transform(Project project) {
        List<PersonDTO> personDTOs = PersonTransformer.transform(project.getPeople());
        List<PairingBoardDTO> pairingBoardDTOs = PairingBoardTransformer.transform(project.getPairingBoards());
        return new ProjectDTO(project.getId(), project.getName(), pairingBoardDTOs, personDTOs);
    }

    public static Project merge(Project project, ProjectDTO projectDTO) {
        List<PairingBoard> pairingBoards = PairingBoardTransformer.reverse(projectDTO.getPairingBoards());
        List<Person> reversePeople = PersonTransformer.reverse(projectDTO.getPeople());
        return new Project(projectDTO.getName(), project.getPassword(), pairingBoards, reversePeople, project.getId());
    }
}
