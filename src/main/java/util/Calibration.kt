package util

import model.Edge
import model.Space
import java.awt.Point
import java.util.*

class Calibration(val deviceId: String) {
  /**
   * TxPower is the expected RSSI value of the device broadcasting at full power from a distance of 1 meter from the
   * receiver.  This field is often included as part of the BLE broadcast, but the actual value requires empirical
   * testing by the manufacturer which is often not done.
   *
   * We'll assume that all devices are of the same make and model, thereby making the txPower universal.
   *
   * Apple recommends a default value of -59 when the actual TxPower is unknown, so that will be our starting point.
   */
  internal var txPower = DEFAULT_TX_POWER
  /**
   * This value is the amount of interference in a particular location, with 2.0 representing "open air" with no
   * interference.  While interference can vary within regions of a single location with lots of electronics or heavy
   * metal equipment, for the sake of this project we'll assume that any variance is negligible and so treat the
   * decayFactor as being universal.
   */
  internal var decayFactor = DEFAULT_DECAY_FACTOR

  fun onNewData(data: HashMap<String, HashMap<String, Int>>) {

    val edgeMap = HashMap<String, LinkedList<Edge>>()

    data.forEach { scannerId, peripherals ->
      edgeMap[scannerId] = LinkedList()
      val scannerPosition = Space.position(scannerId)
      peripherals.forEach { peripheralId, rssi ->
        val peripheralPosition = Space.position(peripheralId)

        val edge = Edge(Point(scannerPosition.x, scannerPosition.y),
                        Point(peripheralPosition.x, peripheralPosition.y),
                        Calc.euclideanDistance(scannerPosition, peripheralPosition),
                        Calc.distance(rssi, this),
                        rssi
        )

        edgeMap[scannerId]!!.add(edge)


      }
    }
    if (deviceId == "a") {
      //      println(edgeMap)

      edgeMap.forEach { _, edge ->
        println(edge)
      }
    }
  }

  companion object {

        private const val DEFAULT_TX_POWER = -59
        private const val DEFAULT_DECAY_FACTOR = 2.0

//    private const val DEFAULT_TX_POWER = -63
//    private const val DEFAULT_DECAY_FACTOR = 2.302
  }
}
