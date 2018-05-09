package model

import java.awt.Point
import java.util.*

object Space {

  // 63,23 put all four nodes in a good spot in regards to errors with integer truncation
  const val X = 63
  const val Z = 23

  val DEVICE_POSITIONS: HashMap<String, Point> = HashMap()

  fun position(deviceId: String): Point = DEVICE_POSITIONS[deviceId]!!
}
