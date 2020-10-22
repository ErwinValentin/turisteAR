package com.valentingonzalez.turistear.models

class Usuario {
    var id: String? = null
    var nombre: String? = null
    var email: String? = null
    var favoritos: HashMap<String, FavoritoUsuario>? = null
    var objetos: HashMap<String, ShopItem>? = null
    var nivelActual: Int? = null
    var puntosActuales: Int? = null
    var puntosTotales: Int? = null
    constructor()
    constructor(id: String?, nombre: String?, email: String?){
        this.id = id
        this.nombre = nombre
        this.email = email
    }

    constructor(id: String?, nombre: String?, email: String?, favoritos: HashMap<String, FavoritoUsuario>?, objetos: HashMap<String, ShopItem>?, nivelActual: Int?, puntosActuales: Int?, puntosTotales: Int?) {
        this.id = id
        this.nombre = nombre
        this.email = email
        this.favoritos = favoritos
        this.objetos = objetos
        this.nivelActual = nivelActual
        this.puntosActuales = puntosActuales
        this.puntosTotales = puntosTotales
    }

    override fun toString(): String {
        return "Usuario(id=$id, nombre=$nombre, email=$email, favoritos=$favoritos, objetos=$objetos, nivelActual=$nivelActual, puntosActuales=$puntosActuales, puntosTotales=$puntosTotales)"
    }

}