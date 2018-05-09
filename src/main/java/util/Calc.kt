package util

import device.Device
import java.awt.Point

object Calc {

  private const val INTERFERENCE_MULTIPLIER = 10.0

  fun distance(rssi: Int, calibration: Calibration): Double {
    return distance(rssi, calibration.txPower, calibration.decayFactor)
  }

  fun distance(rssi: Int, txPower: Int, decayFactor: Double): Double {
    return Math.pow(INTERFERENCE_MULTIPLIER,
                    (txPower - rssi) / (decayFactor * INTERFERENCE_MULTIPLIER))
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


  fun euclideanDistance(first: Device, second: Device): Double =
    euclideanDistance(first.position, second.position)

  fun euclideanDistance(first: Point, second: Point): Double {
    val x = first.x - second.x
    val y = first.y - second.y

    return Math.sqrt((x * x) + (y * y).toDouble())
  }
}
