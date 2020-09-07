package com.valentingonzalez.turistear.models

class Usuario {
    var id: String? = null
    var nombre: String? = null
    var email: String? = null

    constructor() {}
    constructor(id: String?, nombre: String?, email: String?) {
        this.id = id
        this.nombre = nombre
        this.email = email
    }

    override fun toString(): String {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                '}'
    }
}