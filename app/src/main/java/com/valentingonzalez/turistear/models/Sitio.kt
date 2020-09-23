package com.valentingonzalez.turistear.models

class Sitio {
    constructor()
    constructor(descripcion: String?, latitud: Double?, longitud: Double?, nombre: String?, recursos: List<Recurso>?, tipo: String?, rating: Double?) {
        this.descripcion = descripcion
        this.latitud = latitud
        this.longitud = longitud
        this.nombre = nombre
        this.recursos = recursos
        this.tipo = tipo
        this.rating = rating
    }

    var descripcion: String? = null
    var latitud: Double? = null
    var longitud: Double? = null
    var nombre: String? = null
    var recursos: List<Recurso>? = null
    var tipo: String? = null
    var rating: Double? = null
    override fun toString(): String {
        return "Sitio(descripcion=$descripcion, latitud=$latitud, longitud=$longitud, nombre=$nombre, recursos=$recursos, tipo=$tipo, rating=$rating)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sitio

        if (descripcion != other.descripcion) return false
        if (latitud != other.latitud) return false
        if (longitud != other.longitud) return false
        if (nombre != other.nombre) return false
        if (recursos != other.recursos) return false
        if (tipo != other.tipo) return false
        if (rating != other.rating) return false

        return true
    }

    override fun hashCode(): Int {
        var result = descripcion?.hashCode() ?: 0
        result = 31 * result + (latitud?.hashCode() ?: 0)
        result = 31 * result + (longitud?.hashCode() ?: 0)
        result = 31 * result + (nombre?.hashCode() ?: 0)
        result = 31 * result + (recursos?.hashCode() ?: 0)
        result = 31 * result + (tipo?.hashCode() ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        return result
    }


}