import model.Edge
import util.Calc
import util.Calibration
import util.Config
import util.Config.DISTANCE_VARIANCE
import util.Config.MAX_X
import util.Config.MAX_Y
import util.Config.RSSI_SAMPLE_SIZE
import util.Solver.simulateAnnealing
import util.Solver.trilaterate
import java.util.LinkedList
import java.util.concurrent.ThreadLocalRandom

object App {

  val threadLocalRandom = ThreadLocalRandom.current()

  private val scanners = listOf(Pair(MAX_X, 0), Pair(0, MAX_Y), Pair(MAX_X, MAX_Y))
  private val peripheral = Pair(35, 53)

  private val euclideanDistances = scanners.map { pair -> Calc.euclideanDistance(Pair(0, 0), pair) }
  private val rssiConfigDistanceMap = HashMap<Double, Double>()
  private val configDistances = LinkedList<Double>()

  val rssiEuclideanDistanceMap = HashMap<Double, Double>()

  private var peripheralEdges = LinkedList<Edge>()

  private var configAverageError = 0.0

  private val randomVariance: Double
    get() = threadLocalRandom.nextDouble(DISTANCE_VARIANCE * 2) - DISTANCE_VARIANCE

  private lateinit var calibration: Calibration

  @JvmStatic
  fun main(args: Array<String>) {

    createTrainingData()


    calibration = simulateAnnealing()

    rssiEuclideanDistanceMap.forEach { (rssi, euclideanDistance) ->
      printError("calibration",
                 calibration.rssiDistanceMap[rssi]!!,
                 euclideanDistance,
                 calibration.rssiErrorMap[rssi]!!)
    }

    println("\nbest calibration -> txPower = ${calibration.txPower}\tdecay = ${calibration.decay}")

    println("\nconfig average error --> $configAverageError\ncalib  average error --> ${calibration.averageError}")

    createPeripheralData()
    println()
    trilaterate(peripheralEdges)
  }

  private fun createTrainingData() {
    val configErrors = LinkedList<Double>()

    euclideanDistances.forEach { euclideanDistance ->
      val averageRssi = randomRssiStream(euclideanDistance).average()
      rssiEuclideanDistanceMap[averageRssi] = euclideanDistance

      val configDistance = Calc.distance(averageRssi, Config.ACTUAL_TX_POWER, Config.ACTUAL_DECAY)
      configDistances.add(configDistance)
      rssiConfigDistanceMap[averageRssi] = configDistance

      val configError = Calc.errorRate(euclideanDistance, configDistance)
      configErrors.add(configError)

      printError("config", configDistance, euclideanDistance, configError)
    }
    println()

    configAverageError = configErrors.average()
  }

  private fun createPeripheralData() {
    var averageRssi: Double
    var euclideanDistance: Double
    var calculatedDistance: Double

    LinkedList(scanners).apply { add(Pair(0, 0)) }.forEach { scanner ->
      euclideanDistance = Calc.euclideanDistance(scanner, peripheral)
      averageRssi = randomRssiStream(euclideanDistance).average()
      calculatedDistance = Calc.distance(averageRssi, calibration)
      peripheralEdges.add(Edge(scanner, peripheral, averageRssi, calculatedDistance))
    }
  }

  private fun randomRssiStream(euclideanDistance: Double): LinkedList<Int> {
    val rssis = LinkedList<Int>()
    repeat(RSSI_SAMPLE_SIZE, {
      rssis.add(Calc.rssi(Config.ACTUAL_TX_POWER, euclideanDistance + randomVariance, Config.ACTUAL_DECAY))
    })
    return rssis
  }

  private fun printError(label: String, labeledDistance: Double, distance: Double, error: Double) {
    println("${label.padEnd(12)} distance --> ${labeledDistance.toString().padEnd(20)}\t euclidean distance " + "-->${distance.toString().padEnd(
        20)}\terror --> $error")
  }

}
