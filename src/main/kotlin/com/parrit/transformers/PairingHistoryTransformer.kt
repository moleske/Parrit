package com.parrit.transformers

import com.parrit.DTOs.PairingHistoryDTO
import com.parrit.entities.PairingHistory

import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

object PairingHistoryTransformer {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    init {
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun transform(pairingHistory: PairingHistory): PairingHistoryDTO {
        val pairingHistoryDTO = PairingHistoryDTO()
        pairingHistoryDTO.pairingTime = simpleDateFormat.format(pairingHistory.timestamp)
        pairingHistoryDTO.people = PersonTransformer.transform(pairingHistory.people)
        pairingHistoryDTO.pairingBoardName = pairingHistory.pairingBoardName
        return pairingHistoryDTO
    }

    fun transform(pairingHistories: List<PairingHistory>?): List<PairingHistoryDTO> {
        if (pairingHistories == null || pairingHistories.isEmpty()) return emptyList()
        return pairingHistories.map { transform(it) }
    }

}
