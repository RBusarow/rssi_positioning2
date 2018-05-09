import device.Device
import model.Space
import util.Comms
import java.awt.Point
import java.util.*

object App {

  private lateinit var devices: LinkedList<Device>

  @JvmStatic
  fun main(args: Array<String>) {

    createDevices()

    repeat(1, {
      Comms.broadcast()
      devices.forEach { it.sync() }
    })

  }

  private fun createDevices() {
    devices = LinkedList()
    var device: Device
    for (preset in Preset.values()) {
      device = Device(preset.id)
      Comms.receivers.add(device)
      Comms.broadcasters.add(device)
      Space.DEVICE_POSITIONS[preset.id] = Point(preset.x, preset.z)
      devices.add(device)
    }
  }

  internal enum class Preset(val id: String, val x: Int, val z: Int) {
    A("a", 0, 0), B("b", Space.X, 0), C("c", 0, Space.Z), D("d", Space.X, Space.Z)
  }
}
