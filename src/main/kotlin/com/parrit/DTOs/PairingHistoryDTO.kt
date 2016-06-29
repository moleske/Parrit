package com.parrit.DTOs

class PairingHistoryDTO(
        var pairingTime: String = "",
        var people: List<PersonDTO> = emptyList(),
        var pairingBoardName: String = ""
)
