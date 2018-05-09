package device

import App
import model.Broadcast
import model.Space
import util.Calc
import util.Calibration
import util.Comms
import util.Config
import util.Config.RSSI_HISTORY_MAX
import java.awt.Point
import java.util.*
import kotlin.collections.HashMap

class Device(val id: String) {

  private val receivers = HashMap<String, Device>()
  private val broadcasters = HashMap<String, Device>()

  var rssi: Int = 0
    private set(value) {
      rssiHistory.add(value)
      if (rssiHistory.size > RSSI_HISTORY_MAX) {
        rssiHistory.removeFirst()
      }

      field = rssiHistory.sum() / rssiHistory.size
    }

  private val rssiHistory = LinkedList<Int>()

  private val calibration = Calibration(id)

  val stationary =
    id == App.Preset.A.id || id == App.Preset.B.id || id == App.Preset.C.id || id == App.Preset.D.id

  val position: Point get() = Space.DEVICE_POSITIONS[id]!!

  fun sync() {
    Comms.share(this, broadcasters.values)
  }

  fun onReceiveBroadcast(broadcast: Broadcast) {
    if (!broadcasters.containsKey(broadcast.id)) {
      broadcasters[broadcast.id] = Device(broadcast.id)
    }
    broadcasters[broadcast.id]?.let { broadcaster ->
      broadcaster.rssi = broadcast.rssi
      //       printResults(broadcast.id, broadcaster)
      //       printNewRssi(broadcaster, broadcast)
    }
  }

  private fun printNewRssi(peripheral: Device, broadcast: Broadcast) {
    if (id == "a" && peripheral.id == "d") {
      println("device --> ${peripheral.id}\tnew --> " + "${broadcast.rssi}\taverage rssi --> " + "${peripheral.rssi}")
    }
  }

  private fun printResults(filteredDeviceId: String, peripheral: Device?) {
    if (id == "a" && filteredDeviceId == "b") {
      println("device $id saw device -> ${peripheral?.id} with rssi -> ${peripheral?.rssi.toString().padEnd(
        6)}\t\tcalib dist  ->  ${Calc.distance(peripheral!!.rssi,
                                               calibration.txPower,
                                               calibration.decayFactor).toInt()}\t\tconfig dist -> ${Calc.distance(
        peripheral.rssi,
        Config.ACTUAL_TX_POWER,
        Config.ACTUAL_DECAY_FACTOR).toInt()}\t\teuclid dist -> ${Calc.euclideanDistance(this,
                                                                                        peripheral)}")
    }
  }

  fun onReceiveData(nearbyBroadcasters: HashMap<Device, Collection<Device>>) {
    val data = HashMap<String, HashMap<String, Int>>()

    nearbyBroadcasters.forEach { device, peripherals ->
      data[device.id] = HashMap()
      peripherals.forEach { peripheral -> data[device.id]!![peripheral.id] = peripheral.rssi }
    }
    calibration.onNewData(data)

    //    if (id == "a") {
    //      println(data)
    //    }
  }
}
