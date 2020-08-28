package com.valentingonzalez.turistear.models

class Usuario {
    var id: String? = null
    var name: String? = null
    var email: String? = null

    constructor() {}
    constructor(id: String?, name: String?, email: String?) {
        this.id = id
        this.name = name
        this.email = email
    }

    override fun toString(): String {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}'
    }
}