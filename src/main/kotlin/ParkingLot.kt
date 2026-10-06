import kotlinx.coroutines.delay

data class ParkingRecord(val plate: String, val type: String, val hours: Int, val fee: Int)

sealed class ParkingResult {
    data class Parked(val spot: Int, val ticket: Int) : ParkingResult()
    data class AlreadyParked(val plate: String) : ParkingResult()
    object LotFull : ParkingResult()
}

object TicketCounter {
    private var next = 1

    fun nextTicket(): Int = next++
}

class ParkingLot(val capacity: Int) {
    private val spots = mutableMapOf<Int, Vehicle>() // spot number -> vehicle
    private val records = mutableListOf<ParkingRecord>()

    // Read-only copy
    val history: List<ParkingRecord>
        get() = records.toList()

    val occupied: Int
        get() = spots.size

    // Simulated delay
    suspend fun scanPlate(vehicle: Vehicle) {
        delay(500L)
        println("Camera scanned plate ${vehicle.plate}")
    }

    fun park(vehicle: Vehicle): ParkingResult {
        if (spots.values.any { it.plate == vehicle.plate }) return ParkingResult.AlreadyParked(vehicle.plate)
        if (occupied >= capacity) return ParkingResult.LotFull

        var spot = 1
        while (spots.containsKey(spot)) {
            spot++
        }
        spots[spot] = vehicle
        return ParkingResult.Parked(spot, TicketCounter.nextTicket())
    }

    fun leave(plate: String, hours: Int): ParkingRecord? {
        require(hours > 0) { "Parking hours must be positive, got $hours" }
        val entry = spots.entries.find { it.value.plate == plate } ?: return null
        spots.remove(entry.key)

        val vehicle = entry.value
        val record = ParkingRecord(vehicle.plate, vehicle.type, hours, vehicle.fee(hours))
        records.add(record)
        return record
    }

    fun findVehicles(predicate: (Vehicle) -> Boolean): List<Vehicle> = spots.values.filter(predicate)

    fun totalRevenue(): Int =
        if (records.isEmpty()) 0 else records.map { it.fee }.reduce { sum, fee -> sum + fee }
}
