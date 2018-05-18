package util

import App.threadLocalRandom
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver
import com.lemmingapex.trilateration.TrilaterationFunction
import model.Edge
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer
import java.util.Arrays

private const val STARTING_TEMP = 1000000.0
private const val COOLING_RATE = 0.00001

object Solver {

  private val updateTxPower: Boolean
    get() = threadLocalRandom.nextBoolean()

  private val randomTxPower: Int get() = threadLocalRandom.nextInt(Config.TX_MAX - Config.TX_MIN) + Config.TX_MIN
  private val randomDecay: Double get() = threadLocalRandom.nextDouble(Config.DECAY_MAX - Config.DECAY_MIN) + Config.DECAY_MIN

  fun simulateAnnealing(): Calibration {
    var temperature = STARTING_TEMP

    var currentCalibration = Calibration()
    var bestCalibration = currentCalibration.copy()
    var newCalibration: Calibration

    while (temperature > 1) {

      newCalibration = if (updateTxPower) {
        bestCalibration.copy(txPower = randomTxPower)
      } else {
        bestCalibration.copy(decay = randomDecay)
      }

      if (acceptanceProbability(currentCalibration.averageError,
                                bestCalibration.averageError,
                                temperature) > Math.random()) {
        currentCalibration = newCalibration.copy()
      }

      if (currentCalibration.averageError < bestCalibration.averageError) {
        bestCalibration = currentCalibration.copy()
      }
      temperature *= 1 - COOLING_RATE
    }

    return bestCalibration
  }

  private fun acceptanceProbability(currentDistance: Double, newDistance: Double, temperature: Double): Double {
    return if (newDistance < currentDistance) {
      1.0
    } else Math.exp((currentDistance - newDistance) / temperature)
  }

  fun trilaterate(edges: List<Edge>) {

    edges.forEach { println(it) }
    println()

    val pointsArray = edges.map { edge -> edge.a.toDoubleArray() }.toTypedArray()

    val calibratedDistances = edges.map { edge -> edge.calculatedDistance }.toDoubleArray()
    val configDistances = edges.map { edge -> edge.configDistance }.toDoubleArray()
    val euclideanDistances = edges.map { edge -> edge.euclideanDistance }.toDoubleArray()

    trilaterateAndPrint("config", configDistances, pointsArray)
    trilaterateAndPrint("euclidean", euclideanDistances, pointsArray)
    trilaterateAndPrint("calibrated", calibratedDistances, pointsArray)
  }

  private fun trilaterateAndPrint(label: String, distances: DoubleArray, pointsArray: Array<DoubleArray>) {
    println("$label results:")
    println("distances      --> ${Arrays.toString(distances)}")
    val solver =
        NonLinearLeastSquaresSolver(TrilaterationFunction(pointsArray, distances), LevenbergMarquardtOptimizer())

    val optimum = solver.solve()

    val centroid = optimum.point.toArray()

    println("points         --> ${Arrays.toString(centroid)}")
    println("std. deviation --> ${optimum.getSigma(0.0)}")
    println()
  }

  private fun Pair<Int, Int>.toDoubleArray(): DoubleArray {
    val arr = DoubleArray(2)
    arr[0] = first.toDouble()
    arr[1] = second.toDouble()
    return arr
  }
}
