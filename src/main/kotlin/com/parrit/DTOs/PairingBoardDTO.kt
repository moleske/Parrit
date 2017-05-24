package com.parrit.DTOs


class PairingBoardDTO (
    var id: Long = 0,
    var people: List<PersonDTO> = emptyList(),
    var exempt: Boolean = false,
    var name: String = ""
)
