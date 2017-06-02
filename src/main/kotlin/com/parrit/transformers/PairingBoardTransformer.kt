package com.parrit.transformers

import com.parrit.DTOs.PairingBoardDTO
import com.parrit.entities.PairingBoard

object PairingBoardTransformer {

    fun transform(pairingBoard: PairingBoard): PairingBoardDTO {
        val pairingBoardDTO = PairingBoardDTO()
        pairingBoardDTO.id = pairingBoard.id
        pairingBoardDTO.exempt = pairingBoard.isExempt
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
        pairingBoard.isExempt = pairingBoardDTO.exempt
        pairingBoard.people = PersonTransformer.reverse(pairingBoardDTO.people)
        return pairingBoard
    }

    fun reverse(pairingBoardDTOs: List<PairingBoardDTO>?): MutableList<PairingBoard> {
        when {
            pairingBoardDTOs == null || pairingBoardDTOs.isEmpty() -> return mutableListOf()
            else -> return pairingBoardDTOs.map { reverse(it) }.toMutableList()
        }
    }
}
