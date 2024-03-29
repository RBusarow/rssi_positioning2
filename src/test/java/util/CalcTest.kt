package util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import util.Config.ACTUAL_DECAY
import util.Config.ACTUAL_TX_POWER
import java.util.Arrays

class CalcTest {

  @Test
  fun distance() {
    Arrays.stream(Tuple.values()).forEach({ tuple ->
                                            assertEquals(tuple.toString(),
                                                         tuple.distance,
                                                         Calc.distance(tuple.rssi.toDouble(),
                                                                       tuple.txPower,
                                                                       tuple.decayFactor),
                                                         ERROR_TOLERANCE)
                                          })
  }

  @Test
  fun txPower() {
    Arrays.stream(Tuple.values()).forEach({ tuple ->
                                            assertEquals(tuple.toString(),
                                                         tuple.txPower,
                                                         Calc.txPower(tuple.rssi, tuple.distance, tuple.decayFactor))
                                          })
  }

  @Test
  fun rssi() {
    Arrays.stream(Tuple.values()).forEach({ tuple ->
                                            assertEquals(tuple.toString(),
                                                         tuple.rssi,
                                                         Calc.rssi(tuple.txPower, tuple.distance, tuple.decayFactor))
                                          })
  }

  @Test
  fun decayFactor() {
    (0..100).forEach { distance ->
      println("$distance , ${Calc.rssi(Config.ACTUAL_TX_POWER, distance.toDouble(), Config.ACTUAL_DECAY)}")
    }

    Arrays.stream(Tuple.values()).forEach({ tuple ->
                                            assertEquals(tuple.toString(),
                                                         tuple.decayFactor,
                                                         Calc.decayFactor(tuple.rssi, tuple.txPower, tuple.distance),
                                                         ERROR_TOLERANCE)
                                          })
  }

  @Test
  fun cycles() {
    (1..100).forEach { distance ->
      val rssi = Calc.rssi(ACTUAL_TX_POWER, distance.toDouble(), ACTUAL_DECAY)
      val computedDistance = Calc.distance(rssi.toDouble(), ACTUAL_TX_POWER, ACTUAL_DECAY)

      assertEquals("$distance", distance.toDouble(), computedDistance, distance * 0.1)
    }

    (-127..-1).forEach { rssi ->
      val distance = Calc.distance(rssi.toDouble(), ACTUAL_TX_POWER, ACTUAL_DECAY)
      val computedRssi = Calc.rssi(ACTUAL_TX_POWER, distance, ACTUAL_DECAY)

      assertTrue("$rssi", Math.abs(rssi - computedRssi) <= 1)
    }

    (-127..-1).forEach { rssi ->
      val distance = Calc.distance(rssi.toDouble(), ACTUAL_TX_POWER, ACTUAL_DECAY)
      val txPower = Calc.txPower(rssi, distance, ACTUAL_DECAY)
      val computedRssi = Calc.rssi(txPower, distance, ACTUAL_DECAY)

      assertTrue("$rssi", Math.abs(rssi - computedRssi) <= 1)
    }
  }

  @Test
  fun euclideanDistance() {

    assertEquals(5.0, Calc.euclideanDistance(Pair(0, 0), Pair(0, 5)), 0.1)
    assertEquals(5.0, Calc.euclideanDistance(Pair(0, 0), Pair(5, 0)), 0.1)
    assertEquals(5.0, Calc.euclideanDistance(Pair(5, 0), Pair(0, 0)), 0.1)
    assertEquals(5.0, Calc.euclideanDistance(Pair(0, 5), Pair(0, 0)), 0.1)

    assertEquals(1.4142135623730951, Calc.euclideanDistance(Pair(1, 1), Pair(2, 2)), 0.1)
    assertEquals(5.0, Calc.euclideanDistance(Pair(1, 1), Pair(4, 5)), 0.1)

    assertEquals(98.99494936611666, Calc.euclideanDistance(Pair(0, 0), Pair(70, 70)), 0.1)
    assertEquals(98.99494936611666, Calc.euclideanDistance(Pair(0, 0), Pair(-70, -70)), 0.1)

    assertEquals(2.8284271247461903, Calc.euclideanDistance(Pair(-1, -2), Pair(-3, -4)), 0.1)
    assertEquals(7.211102550927978, Calc.euclideanDistance(Pair(1, 2), Pair(-3, -4)), 0.1)
    assertEquals(2.8284271247461903, Calc.euclideanDistance(Pair(1, 2), Pair(3, 4)), 0.1)
  }

  internal enum class Tuple(val rssi: Int, val txPower: Int, val decayFactor: Double, val distance: Double) {
    ONE(-50, -59, 2.0, 0.35481338923357547),
    TWO(-75, -59, 2.0, 6.309573444801933),
    THREE(-75, -59, 2.3, 4.961947603002903),
    FOUR(-100, -59, 2.3, 60.61898993497572),
    FIVE(-100, -50, 2.3, 149.2495545051829),
    SIX(-100, -70, 2.3, 20.15337685941733)
  }

  companion object {

    private const val ERROR_TOLERANCE = 0.01
  }
}
