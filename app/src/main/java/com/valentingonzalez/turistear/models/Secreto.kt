package com.valentingonzalez.turistear.models

class Secreto{
    constructor() {}
    constructor(descripcion: String?, latitud: Double?, longitud: Double?, nombre: String?, recursos: List<Recurso>?) {
        this.descripcion = descripcion
        this.latitud = latitud
        this.longitud = longitud
        this.nombre = nombre
        this.recursos = recursos
    }

    var descripcion: String? = null
    var latitud: Double? = null
    var longitud: Double? = null
    var nombre: String? = null
    var recursos: List<Recurso>? = null
    override fun toString(): String {
        return "Secreto(descripcion=$descripcion, latitud=$latitud, longitud=$longitud, nombre=$nombre, recursos=$recursos)"
    }
}