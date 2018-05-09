package util

import util.Calc.euclideanDistance
import java.awt.Point

object Utils {

  fun rssiFromSource(sourcePosition: Point, receiverPosition: Point, variance: Int
  ): Int {
    val euclideanDistance = euclideanDistance(sourcePosition, receiverPosition)
    val distanceRssi =
      Calc.rssi(Config.ACTUAL_TX_POWER, euclideanDistance, Config.ACTUAL_DECAY_FACTOR)
    return distanceRssi + variance
  }
}