package com.valentingonzalez.turistear.models

import android.location.Location
import java.io.Serializable

class Secreto : Serializable{
    constructor()
    constructor(descripcion: String?, latitud: Double?, longitud: Double?, altitud: Double?, nombre: String?, recursos: List<Recurso>?) {
        this.descripcion = descripcion
        this.latitud = latitud
        this.longitud = longitud
        this.altitud = altitud
        this.nombre = nombre
        this.recursos = recursos
    }

    var descripcion: String? = null
    var latitud: Double? = null
    var longitud: Double? = null
    var altitud: Double? = null
    var nombre: String? = null
    var recursos: List<Recurso>? = null

    fun getLocation(): Location {
        val secretLocation = Location("")
        secretLocation.latitude = this.latitud!!
        secretLocation.longitude = this.longitud!!
        secretLocation.altitude = this.altitud!!

        return secretLocation
    }
    override fun toString(): String {
        return "Secreto(descripcion=$descripcion, latitud=$latitud, longitud=$longitud, nombre=$nombre, recursos=$recursos)"
    }
}