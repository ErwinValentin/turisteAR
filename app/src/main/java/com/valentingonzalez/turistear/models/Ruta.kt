package com.valentingonzalez.turistear.models

class Ruta {
    constructor(nombre: String?, puntos: List<PuntoRuta>?) {
        this.nombre = nombre
        this.puntos = puntos
    }

    constructor()

    var nombre : String ? = null
    var puntos : List<PuntoRuta>? = null

    override fun toString(): String {
        return "Ruta(nombre=$nombre, puntos=$puntos)"
    }


}