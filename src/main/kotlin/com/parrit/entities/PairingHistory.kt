package com.parrit.entities

import javax.persistence.*
import java.sql.Timestamp

@Entity
class PairingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(targetEntity = Project::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    var project: Project? = null

    @ManyToMany(targetEntity = Person::class, fetch = FetchType.LAZY)
    @JoinTable(inverseJoinColumns = arrayOf(JoinColumn(name = "person_id")))
    var people: List<Person>? = null

    var timestamp: Timestamp? = null

    var pairingBoardName: String? = null

    constructor() {
    }

    constructor(project: Project, people: List<Person>, timestamp: Timestamp, pairingBoardName: String) {
        this.project = project
        this.people = people
        this.timestamp = timestamp
        this.pairingBoardName = pairingBoardName
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is PairingHistory) return false

        if (id != o.id) return false
        if (if (project != null) project != o.project else o.project != null) return false
        if (if (people != null) people != o.people else o.people != null) return false
        if (if (timestamp != null) !timestamp!!.equals(o.timestamp) else o.timestamp != null)
            return false
        return if (pairingBoardName != null) pairingBoardName == o.pairingBoardName else o.pairingBoardName == null

    }

    override fun hashCode(): Int {
        var result = (id xor id.ushr(32)).toInt()
        result = 31 * result + if (project != null) project!!.hashCode() else 0
        result = 31 * result + if (people != null) people!!.hashCode() else 0
        result = 31 * result + if (timestamp != null) timestamp!!.hashCode() else 0
        result = 31 * result + if (pairingBoardName != null) pairingBoardName!!.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "PairingHistory{id=$id, project=$project, people=$people, timestamp=$timestamp, pairingBoardName='$pairingBoardName'}"
    }
}
