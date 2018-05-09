package device

import model.BroadcastData
import util.Broadcasts
import util.Calibration
import util.Config
import util.RssiCalculator
import java.util.HashMap
import java.util.LinkedList

class Device(val macAddress: String) {

  private val receivers = HashMap<String, Device>()
  private val peripherals = HashMap<String, Device>()

  val position: Pair<Int, Int>? = null

  var rssi: Int = 0
    private set(value) {
      rssiHistory.add(value)
      if (rssiHistory.size > RSSI_HISTORY_MAX) {
        rssiHistory.removeFirst()
      }

      field = rssiHistory.stream().mapToInt { oldRssi -> oldRssi }.sum() / rssiHistory.size
    }

  private val rssiHistory = LinkedList<Int>()

  private val calibration = Calibration()

  fun sync() {
    Broadcasts.share(this, peripherals.values)
  }

  fun onReceiveBroadcast(broadcastData: BroadcastData) {
    if (!peripherals.containsKey(broadcastData.macAddress)) {
      peripherals[broadcastData.macAddress] = Device(broadcastData.macAddress)
    }
    val peripheral = peripherals[broadcastData.macAddress]
    peripheral?.rssi = broadcastData.rssi

    if (macAddress == "a" && broadcastData.macAddress == "d") {
      println("$macAddress saw -> ${peripheral?.macAddress} as -> ${peripheral?.rssi}\t\tcalc dist -> ${RssiCalculator.distance(
        peripheral?.rssi ?: 0,
        calibration.txPower,
        calibration.decayFactor
      ).toInt()}\t\tactual dist -> ${RssiCalculator.distance(peripheral?.rssi ?: 0,
                                                                Config.ACTUAL_TX_POWER,
                                                                Config.ACTUAL_DECAY_FACTOR
      ).toInt()}"
      )
    }
  }

  fun onReceiveData(nearbyBroadcasters: HashMap<Device, Collection<Device>>) {
    //pendnigSyncs.forEach((broadcaster, neighbors) -> {
    //  neighbors.forEach(neighbor -> {
    //
    //  });
    //});

  }

  companion object {

    private val RSSI_HISTORY_MAX = 20
  }
}
