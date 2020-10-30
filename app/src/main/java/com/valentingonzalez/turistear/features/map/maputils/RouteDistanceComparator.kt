package com.valentingonzalez.turistear.features.map.maputils

import com.google.android.gms.maps.model.LatLng
import com.valentingonzalez.turistear.models.PuntoRuta
import kotlin.math.*

class RouteDistanceComparator(val current: LatLng): Comparator<PuntoRuta>{

    override fun compare(punto1: PuntoRuta?, punto2: PuntoRuta?): Int {
        val p1 = LatLng(punto1?.latitud!!, punto1.longitud!!)
        val p2 = LatLng(punto2?.latitud!!, punto2.longitud!!)

        val distanceToPlace1 = distance(current,p1)
        val distanceToPlace2 = distance(current,p2)
        return (distanceToPlace1 - distanceToPlace2).toInt()
    }

    private fun distance(from: LatLng, to: LatLng ) : Double{
        val radius = 6378137
        val deltaLat = from.latitude - to.latitude
        val deltaLon = from.longitude - to.longitude
        val angle = 2 * asin( sqrt(
                sin(deltaLat / 2).pow(2) +
                   cos(from.latitude) * cos(to.latitude) *
                        sin(deltaLon / 2).pow(2)
        ))
        return radius * angle
    }
}