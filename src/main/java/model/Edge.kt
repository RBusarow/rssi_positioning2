package model

import util.Calc
import util.Config

data class Edge(val a: Pair<Int, Int>, val b: Pair<Int, Int>, val rssi: Double, val calculatedDistance: Double) {

  val configDistance: Double = Calc.distance(rssi, Config.ACTUAL_TX_POWER, Config.ACTUAL_DECAY)
  val euclideanDistance: Double = Calc.euclideanDistance(a, b)

  override fun toString(): String {
    return "Edge(a=$a, b=$b, rssi=$rssi, calculatedDistance=$calculatedDistance, configDistance=$configDistance, euclideanDistance=$euclideanDistance)"
  }

}
