package com.valentingonzalez.turistear.models

class Sitio {
    constructor(descripcion: String?, latitud: Float?, longitud: Float?, nombre: String?, recursos: List<Recurso>?, tipo: String?) {
        this.descripcion = descripcion
        this.latitud = latitud
        this.longitud = longitud
        this.nombre = nombre
        this.recursos = recursos
        this.tipo = tipo
    }

    var descripcion: String? = null
    var latitud: Float? = null
    var longitud: Float? = null
    var nombre: String? = null
    var recursos: List<Recurso>? = null
    var tipo: String? = null

    override fun toString(): String {
        return "Sitio(descripcion=$descripcion, latitud=$latitud, longitud=$longitud, nombre=$nombre, recursos=$recursos, tipo=$tipo)"
    }
}