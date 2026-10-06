import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("=== City Parking Lot ===")

    val lot = ParkingLot(capacity = 4)
    val arriving: List<Vehicle> = listOf(
        Car("123ABC01", "Aliya"),
        Motorcycle("777MOT02", "Daniyar"),
        Truck("555TRK02", "Nurlan"),
        Car("909XYZ01", "Aigerim"),
        Car("111AAA05", "Timur"),
    )

    println("\n-- Vehicles arriving --")
    val scans = arriving.map { vehicle -> launch { lot.scanPlate(vehicle) } }
    scans.joinAll()

    println("\n-- Parking --")
    for (vehicle in arriving) {
        when (val result = lot.park(vehicle)) {
            is ParkingResult.Parked -> println("$vehicle parked at spot ${result.spot}, ticket #${result.ticket}")
            is ParkingResult.AlreadyParked -> println("${result.plate} is already inside")
            ParkingResult.LotFull -> println("No free spots for $vehicle")
        }
    }
    when (val result = lot.park(arriving[0])) {
        is ParkingResult.AlreadyParked -> println("${result.plate} is already inside")
        else -> println("Unexpected result: $result")
    }

    val occupancy: Double = lot.occupied.toDouble() / lot.capacity * 100
    println("Occupancy: ${lot.occupied}/${lot.capacity} ($occupancy%)")

    println("\n-- Search with lambdas --")
    val cars = lot.findVehicles { it is Car }
    println("Cars: ${cars.map { it.plate }}")
    val cheap = lot.findVehicles { it.hourlyRate() < 300 }
    println("Cheaper than 300 KZT/hour: ${cheap.map { it.plate }}")

    println("\n-- Vehicles leaving --")
    val stays = mapOf("123ABC01" to 3, "555TRK02" to 1, "777MOT02" to 4, "000NOT00" to 2)
    for ((plate, hours) in stays) {
        val record = lot.leave(plate, hours)
        if (record != null) {
            println("${record.type} ${record.plate} stayed ${record.hours}h and paid ${record.fee} KZT")
        } else {
            println("$plate was not found in the lot")
        }
    }

    println("\n-- Statistics --")
    println("Total revenue: ${lot.totalRevenue()} KZT")
    val bigPayers = lot.history.filter { it.fee >= 1000 }.map { it.plate }
    println("Paid 1000 KZT or more: $bigPayers")
    val typesServed: Set<String> = lot.history.map { it.type }.toSet()
    println("Vehicle types served: $typesServed")
    println("Still parked: ${lot.findVehicles { true }}")
}
