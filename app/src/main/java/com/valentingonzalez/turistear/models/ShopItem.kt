package com.valentingonzalez.turistear.models

class ShopItem{
    constructor()
    constructor(nombre: String?, descipcion: String?, imagen: String?, precio: Long?) {
        this.nombre = nombre
        this.descripcion = descipcion
        this.imagen = imagen
        this.precio = precio
    }

    var nombre: String? = null
    var descripcion: String? = null
    var imagen: String? = null
    var precio: Long? = null
    override fun toString(): String {
        return "ShopItem(nombre=$nombre, descipcion=$descripcion, imagen=$imagen, precio=$precio)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShopItem

        if (nombre != other.nombre) return false
        if (descripcion != other.descripcion) return false
        if (imagen != other.imagen) return false
        if (precio != other.precio) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nombre?.hashCode() ?: 0
        result = 31 * result + (descripcion?.hashCode() ?: 0)
        result = 31 * result + (imagen?.hashCode() ?: 0)
        result = 31 * result + (precio?.hashCode() ?: 0)
        return result
    }


}