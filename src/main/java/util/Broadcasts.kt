package util

import device.Device
import model.BroadcastData
import model.Space
import util.Config.RSSI_VARIANCE
import java.util.HashMap
import java.util.LinkedList
import java.util.concurrent.ThreadLocalRandom

/**
 * This class serves as a surrogate for BLE adapters.  In the real world,
 * a device would advertise and receive broadcasts via its adapter.
 */
object Broadcasts {

  var scanners = LinkedList<Device>()
  var broadcasters = LinkedList<Device>()
  private val pendingSyncs = HashMap<Device, Collection<Device>>()

  private val threadLocalRandom = ThreadLocalRandom.current()

  private val random: Int
    get() = threadLocalRandom.nextInt(RSSI_VARIANCE * 2) - RSSI_VARIANCE

  fun broadcast(testVariance: Int? = null) {
    broadcasters.forEach { broadcaster ->
      scanners.filter { it.macAddress != broadcaster.macAddress }
          .forEach { scanner ->
            val rssi = MiscUtils.rssiFromSource(Space.DEVICE_POSITIONS[broadcaster.macAddress]!!,
                                                Space.DEVICE_POSITIONS[scanner.macAddress]!!,
                                                testVariance ?: random
            )
            scanner.onReceiveBroadcast(BroadcastData(broadcaster.macAddress, rssi))
          }
    }
  }

  fun share(origin: Device, peripherals: Collection<Device>) {
    pendingSyncs[origin] = peripherals
    if (pendingSyncs.size > peripherals.size) {
      scanners.forEach { scanner -> scanner.onReceiveData(HashMap(pendingSyncs)) }
      pendingSyncs.clear()
    }
  }
}
