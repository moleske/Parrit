package com.parrit.transformers;

import com.parrit.DTOs.PairingHistoryDTO;
import com.parrit.DTOs.PersonDTO;
import com.parrit.entities.PairingHistory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PairingHistoryTransformer {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    static {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static PairingHistoryDTO transform(PairingHistory pairingHistory) {
        String pairingTime = simpleDateFormat.format(pairingHistory.getTimestamp());
        List<PersonDTO> personDTOs = PersonTransformer.transform(pairingHistory.getPeople());
        PairingHistoryDTO pairingHistoryDTO = new PairingHistoryDTO(pairingTime, personDTOs, pairingHistory.getPairingBoardName());
        return pairingHistoryDTO;
    }

    public static List<PairingHistoryDTO> transform(List<PairingHistory> pairingHistories) {
        if(pairingHistories == null || pairingHistories.isEmpty()) return Collections.emptyList();
        return pairingHistories.stream()
            .map(PairingHistoryTransformer::transform)
            .collect(Collectors.toList());
    }

}
