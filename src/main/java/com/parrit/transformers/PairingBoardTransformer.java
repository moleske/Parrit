package com.parrit.transformers;

import com.parrit.DTOs.PairingBoardDTO;
import com.parrit.DTOs.PersonDTO;
import com.parrit.entities.PairingBoard;
import com.parrit.entities.Person;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PairingBoardTransformer {

    public static PairingBoardDTO transform(PairingBoard pairingBoard) {
        List<PersonDTO> personDTOs = PersonTransformer.transform(pairingBoard.getPeople());
        return new PairingBoardDTO(pairingBoard.getId(), personDTOs, pairingBoard.getName());
    }

    public static List<PairingBoardDTO> transform(List<PairingBoard> pairingBoards) {
        if(pairingBoards == null || pairingBoards.isEmpty()) return Collections.emptyList();
        return pairingBoards.stream()
                .map(PairingBoardTransformer::transform)
                .collect(Collectors.toList());
    }

    public static PairingBoard reverse(PairingBoardDTO pairingBoardDTO) {
        List<Person> reversePeople = PersonTransformer.reverse(pairingBoardDTO.getPeople());
        return new PairingBoard(pairingBoardDTO.getName(), reversePeople, pairingBoardDTO.getId());
    }

    public static List<PairingBoard> reverse(List<PairingBoardDTO> pairingBoardDTOs) {
        if(pairingBoardDTOs == null || pairingBoardDTOs.isEmpty()) return Collections.emptyList();
        return pairingBoardDTOs.stream()
            .map(PairingBoardTransformer::reverse)
            .collect(Collectors.toList());
    }
}
