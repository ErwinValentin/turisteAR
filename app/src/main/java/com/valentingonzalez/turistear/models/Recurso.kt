package com.valentingonzalez.turistear.models

class Recurso{
    constructor(tipo: String?, valor: String?) {
        this.tipo = tipo
        this.valor = valor
    }

    var tipo: String? = null
    var valor: String? = null
    override fun toString(): String {
        return "Recurso(tipo=$tipo, valor=$valor)"
    }
}