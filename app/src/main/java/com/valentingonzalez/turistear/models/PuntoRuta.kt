package com.valentingonzalez.turistear.models

class PuntoRuta{
    constructor(nombre: String?, latitud: String?, longitud: String?) {
        this.nombre = nombre
        this.latitud = latitud
        this.longitud = longitud
    }

    constructor()

    var nombre : String? = null
    var latitud : String? = null
    var longitud : String? = null

    override fun toString(): String {
        return "Ruta(nombre=$nombre, latitud=$latitud, longitud=$longitud)"
    }
}