package com.valentingonzalez.turistear.models

class SitioDescubierto {
    constructor(fecha: String?, nombre: String?, llave : String?) {
        this.fecha = fecha
        this.nombre = nombre
        this.llave = llave
    }

    constructor()

    var fecha : String? = null
    var nombre : String? = null
    var llave : String? = null

    override fun toString(): String {
        return "SitioDescubierto(fecha=$fecha, nombre=$nombre, llave=$llave)"
    }
}