package com.parrit.transformers

import com.parrit.DTOs.PairingBoardDTO
import com.parrit.entities.PairingBoard

object PairingBoardTransformer {

    fun transform(pairingBoard: PairingBoard): PairingBoardDTO {
        val pairingBoardDTO = PairingBoardDTO()
        pairingBoardDTO.id = pairingBoard.id
        pairingBoardDTO.exempt = pairingBoard.exempt
        pairingBoardDTO.name = pairingBoard.name
        pairingBoardDTO.people = PersonTransformer.transform(pairingBoard.people)
        return pairingBoardDTO
    }

    fun transform(pairingBoards: List<PairingBoard>?): List<PairingBoardDTO> {
        if (pairingBoards == null || pairingBoards.isEmpty()) return emptyList()
        return pairingBoards.map { transform(it) }
    }

    fun reverse(pairingBoardDTO: PairingBoardDTO): PairingBoard {
        val pairingBoard = PairingBoard()
        pairingBoard.id = pairingBoardDTO.id
        pairingBoard.name = pairingBoardDTO.name
        pairingBoard.exempt = pairingBoardDTO.exempt
        pairingBoard.people = PersonTransformer.reverse(pairingBoardDTO.people)
        return pairingBoard
    }

    fun reverse(pairingBoardDTOs: List<PairingBoardDTO>?): List<PairingBoard> {
        when {
            pairingBoardDTOs == null || pairingBoardDTOs.isEmpty() -> return emptyList()
            else -> return pairingBoardDTOs.map { reverse(it) }
        }
    }
}
