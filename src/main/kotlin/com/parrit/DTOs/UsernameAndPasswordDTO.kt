package com.parrit.DTOs

class UsernameAndPasswordDTO {

    var name: String = ""
    var password: String = ""

    constructor() {
    }

    constructor(name: String, password: String) {
        this.name = name
        this.password = password
    }
}
