package util

import device.Device
import model.Broadcast
import util.Config.RSSI_VARIANCE
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * This class serves as a surrogate for BLE adapters.  In the real world,
 * a device would advertise and receive broadcasts via its adapter.
 */
object Comms {

  var receivers = LinkedList<Device>()
  var broadcasters = LinkedList<Device>()
  private val pendingSyncs = HashMap<Device, Collection<Device>>()

  private val threadLocalRandom = ThreadLocalRandom.current()

  private val random: Int
    get() = threadLocalRandom.nextInt(RSSI_VARIANCE * 2) - RSSI_VARIANCE

  fun broadcast(testVariance: Int? = null) {
    broadcasters.forEach { broadcaster ->
      receivers.filter { it.id != broadcaster.id }.forEach { scanner ->
        val rssi =
          Utils.rssiFromSource(broadcaster.position, scanner.position, testVariance ?: random)
        scanner.onReceiveBroadcast(Broadcast(broadcaster.id, rssi))
      }
    }
  }

  fun share(origin: Device, peripherals: Collection<Device>) {
    pendingSyncs[origin] = peripherals
    if (pendingSyncs.size > peripherals.size) {
      receivers.forEach { receiver -> receiver.onReceiveData(HashMap(pendingSyncs)) }
      pendingSyncs.clear()
    }
  }
}
