package util

object Calc {

  private const val INTERFERENCE_MULTIPLIER = 10.0

  fun distance(rssi: Double, calibration: Calibration): Double {
    return distance(rssi, calibration.txPower, calibration.decay)
  }

  fun distance(rssi: Double, txPower: Int, decayFactor: Double): Double {
    return Math.pow(INTERFERENCE_MULTIPLIER, (txPower - rssi) / (decayFactor * INTERFERENCE_MULTIPLIER))
  }

  fun txPower(rssi: Int, distance: Double, decayFactor: Double): Int {
    return (INTERFERENCE_MULTIPLIER * decayFactor * Math.log10(distance)).toInt() + rssi
  }

  fun rssi(txPower: Int, distance: Double, decayFactor: Double): Int {
    return (-(INTERFERENCE_MULTIPLIER * decayFactor * Math.log10(distance) - txPower)).toInt()
  }

  fun decayFactor(rssi: Int, txPower: Int, distance: Double): Double {
    return (txPower - rssi).toDouble() / Math.log10(distance) / INTERFERENCE_MULTIPLIER
  }

  fun errorRate(expected: Double, actual: Double): Double {
    return Math.abs(actual - expected) / expected
  }

  fun euclideanDistance(first: Pair<Int, Int>, second: Pair<Int, Int>): Double {
    val x = first.first - second.first
    val y = first.second - second.second

    return Math.sqrt((x * x) + (y * y).toDouble())
  }
}
