package com.valentingonzalez.turistear.models

import java.util.*

class Comentario {
    constructor()
    constructor(comentario: String?, calificacion: Float?, fecha: String?, nombreSitio: String?, nombreUsuario: String?, titulo: String?) {
        this.comentario = comentario
        this.calificacion = calificacion
        this.fecha = fecha
        this.nombreSitio = nombreSitio
        this.nombreUsuario = nombreUsuario
        this.titulo = titulo
    }

    var comentario: String? = null
    var calificacion: Float?= null
    var fecha: String? = null
    var nombreSitio: String? = null
    var nombreUsuario: String? = null
    var titulo: String?= null

    override fun toString(): String {
        return "Comentario(comentario=$comentario, calificacion=$calificacion, fecha=$fecha, nombreSitio=$nombreSitio, nombreUsuario=$nombreUsuario, titulo=$titulo)"
    }
}