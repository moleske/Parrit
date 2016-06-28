package com.parrit.DTOs

class ProjectDTO {
    var id: Long = 0
    var name: String? = null
    var pairingBoards: List<PairingBoardDTO>? = null
    var people: List<PersonDTO>? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is ProjectDTO) return false

        if (id != o.id) return false
        if (if (name != null) name != o.name else o.name != null) return false
        if (if (pairingBoards != null) pairingBoards != o.pairingBoards else o.pairingBoards != null) return false
        return if (people != null) people == o.people else o.people == null

    }
}
