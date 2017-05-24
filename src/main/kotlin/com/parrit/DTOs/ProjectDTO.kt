package com.parrit.DTOs

data class ProjectDTO (
    var id: Long = 0,
    var name: String = "",
    var pairingBoards: List<PairingBoardDTO> = emptyList(),
    var people: List<PersonDTO> = emptyList()
)
