package com.valentingonzalez.turistear.models

import java.util.*

class Comentario {
    constructor()
    constructor(comentario: String?, calificacion: Float?, fecha: String?, idUsuario: String?, nombreUsuario: String?, titulo: String?) {
        this.comentario = comentario
        this.calificacion = calificacion
        this.fecha = fecha
        this.idUsuario = idUsuario
        this.nombreUsuario = nombreUsuario
        this.titulo = titulo
    }

    var comentario: String? = null
    var calificacion: Float?= null
    var fecha: String? = null
    var idUsuario: String? = null
    var nombreUsuario: String? = null
    var titulo: String?= null

    override fun toString(): String {
        return "Comentario(comentario=$comentario, calificacion=$calificacion, fecha=$fecha, idUsuario=$idUsuario, nombreUsuario=$nombreUsuario, titulo=$titulo)"
    }
}