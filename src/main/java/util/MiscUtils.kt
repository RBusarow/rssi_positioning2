package util

object MiscUtils {

  fun euclideanDistance(first: Pair<Int, Int>, second: Pair<Int, Int>): Double {

    val x = first.first - second.first
    val y = first.second - second.second

    return Math.sqrt((x * x) + (y * y).toDouble())
  }

  fun rssiFromSource(sourcePosition: Pair<Int, Int>, receiverPosition: Pair<Int, Int>, variance: Int): Int {
    val euclideanDistance = euclideanDistance(sourcePosition, receiverPosition)
    val distanceRssi = RssiCalculator.rssi(Config.ACTUAL_TX_POWER, euclideanDistance, Config.ACTUAL_DECAY_FACTOR)
    return distanceRssi + variance
  }
}
