package com.valentingonzalez.turistear.models

class Secreto{
    constructor(descripcion: String?, latitud: Float?, longitud: Float?, nombre: String?, recursos: List<Recurso>?) {
        this.descripcion = descripcion
        this.latitud = latitud
        this.longitud = longitud
        this.nombre = nombre
        this.recursos = recursos
    }

    var descripcion: String? = null
    var latitud: Float? = null
    var longitud: Float? = null
    var nombre: String? = null
    var recursos: List<Recurso>? = null
}