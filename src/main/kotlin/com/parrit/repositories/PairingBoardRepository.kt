package com.parrit.repositories

import com.parrit.entities.PairingBoard
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PairingBoardRepository : CrudRepository<PairingBoard, Long>