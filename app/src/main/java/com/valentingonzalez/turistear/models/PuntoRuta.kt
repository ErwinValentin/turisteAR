package com.valentingonzalez.turistear.models

import java.io.Serializable

class PuntoRuta : Serializable{
    constructor(nombre: String?, latitud: Double?, longitud: Double?) {
        this.nombre = nombre
        this.latitud = latitud
        this.longitud = longitud
    }

    constructor()

    var nombre : String? = null
    var latitud : Double? = null
    var longitud : Double? = null

    override fun toString(): String {
        return "Ruta(nombre=$nombre, latitud=$latitud, longitud=$longitud)"
    }
}