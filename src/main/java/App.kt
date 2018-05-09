import device.Device
import model.Space
import util.Broadcasts
import java.util.*
import java.util.function.Consumer

object App {

  private lateinit var devices: LinkedList<Device>

  @JvmStatic
  fun main(args: Array<String>) {

    createDevices()

    repeat(500, { Broadcasts.broadcast(0) })

    devices.forEach(Consumer<Device> { it.sync() })
  }

  private fun createDevices() {
    devices = LinkedList()
    var device: Device
    for (preset in Preset.values()) {
      device = Device(preset.value)
      Broadcasts.scanners.add(device)
      Broadcasts.broadcasters.add(device)
      Space.DEVICE_POSITIONS[preset.value] = Pair(preset.x, preset.z)
      devices.add(device)
    }
  }

  internal enum class Preset(val value: String, val x: Int, val z: Int) {
    A("a", 0, 0), B("b", Space.X, 0), C("c", 0, Space.Z), D("d", Space.X, Space.Z)
  }
}
