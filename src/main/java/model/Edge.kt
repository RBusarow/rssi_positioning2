package model

import java.awt.Point

data class Edge(val a: Point,
                val b: Point,
                val distance: Double,
                val calculatedDistance: Double,
                val rssi: Int
)
