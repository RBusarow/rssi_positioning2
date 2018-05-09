package util

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver
import com.lemmingapex.trilateration.TrilaterationFunction
import model.Edge
import model.Space
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer
import java.awt.Point
import java.util.*


class Calibration(val deviceId: String) {
  /**
   * TxPower is the expected RSSI value of the device broadcasting at full power from a configDistance of 1 meter from the
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


        edgeMap[scannerId]!!.add(Edge(Point(scannerPosition.x, scannerPosition.y),
                                      Point(peripheralPosition.x, peripheralPosition.y),
                                      Calc.distance(rssi, this),
                                      rssi))
      }
    }
    if (deviceId == "a") {
      //      edgeMap.forEach { _, edge ->
      //        println(edge)
      //      }
      trilaterate(edgeMap[deviceId]!!)
    }
  }

  fun trilaterate(edges: List<Edge>) {

    val pointsArray = edges.map { edge -> edge.b.toDoubleArray() }.toTypedArray()
    val calculatedDistancesArray = edges.map { edge -> edge.calculatedDistance }.toDoubleArray()
    val configDistancesArray = edges.map { edge -> edge.configDistance }.toDoubleArray()
    val euclideanDistancesArray = edges.map { edge -> edge.euclideanDistance }.toDoubleArray()

    pointsArray.forEach { println(Arrays.toString(it)) }
    println(Arrays.toString(euclideanDistancesArray))

    listOf(calculatedDistancesArray,
           configDistancesArray,
           euclideanDistancesArray).forEach { distances ->

      val solver = NonLinearLeastSquaresSolver(TrilaterationFunction(pointsArray, distances),
                                               LevenbergMarquardtOptimizer())

      val optimum = solver.solve()

      val centroid = optimum.point.toArray()

      println(optimum.point.toString())

      val standardDeviation = optimum.getSigma(0.0)
    }
  }

  companion object {

      const val DEFAULT_TX_POWER = -59
      const val DEFAULT_DECAY_FACTOR = 2.0

    //    private const val DEFAULT_TX_POWER = -63
    //    private const val DEFAULT_DECAY_FACTOR = 2.302
  }
}
