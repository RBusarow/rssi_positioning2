package util

object RssiCalculator {

  private const val INTERFERENCE_MULTIPLIER = 10.0

  fun distance(rssi: Int, txPower: Int, decayFactor: Double): Double {
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
}
