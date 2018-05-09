package model

import util.Calc
import util.Config
import java.awt.Point

data class Edge(val a: Point, val b: Point, val calculatedDistance: Double, val rssi: Int) {

  val configDistance: Double
    get() = Calc.distance(rssi,
                          Config.ACTUAL_TX_POWER,
                          Config.ACTUAL_DECAY_FACTOR)
  val euclideanDistance: Double get() = Calc.euclideanDistance(a, b)

}
