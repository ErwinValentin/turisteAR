package com.valentingonzalez.turistear.models

class FavoritoUsuario{
    constructor()
    constructor(llave: String?, nombre: String?, numSecreto: Int?, fechaAgregado: String?) {
        this.llave = llave
        this.nombre = nombre
        this.numSecreto = numSecreto
        this.fechaAgregado = fechaAgregado
    }

    var llave : String? = null // llave del lugar de interes
    var nombre : String? = null
    var numSecreto : Int? = null //0, 1, 2 para secretos, -1 para puntos de interes
    var fechaAgregado: String? = null
    override fun toString(): String {
        return "FavoritoUsuario(llave=$llave, nombre=$nombre, numSecreto=$numSecreto, fechaAgregado=$fechaAgregado)"
    }


}