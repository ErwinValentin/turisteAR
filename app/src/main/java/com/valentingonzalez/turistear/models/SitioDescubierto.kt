package com.valentingonzalez.turistear.models

class SitioDescubierto {
    constructor(fecha: String?, nombre: String?) {
        this.fecha = fecha
        this.nombre = nombre
    }

    constructor()

    var fecha : String? = null
    var nombre : String? = null

    override fun toString(): String {
        return "SitioDescubierto(fecha=$fecha, nombre=$nombre)"
    }
}