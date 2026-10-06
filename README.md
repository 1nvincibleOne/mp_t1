# Parking Lot

A small Kotlin console application that simulates a city parking lot. Vehicles arrive, an entrance camera scans their plates, they park in free spots, and when they leave they pay a fee that depends on the vehicle type. At the end the program prints revenue and other statistics.

## How to run

Requirements: JDK 17 or newer.

**IntelliJ IDEA:** open the project folder (it is imported as a Gradle project), then run `main()` in `src/main/kotlin/Main.kt`.

**Command line:**

```bash
./gradlew run
```

On Windows use `gradlew.bat run`.

## Where the requirements are demonstrated

- **Variables, data types, conditions, loops:** throughout `Main.kt`; `while` loop in `ParkingLot.park()`
- **List, Set, Map:** `arriving` (List), `typesServed` (Set), `stays` (Map) in `Main.kt`
- **map, filter, reduce:** `ParkingLot.totalRevenue()` and the statistics in `Main.kt`
- **Higher-order functions and lambdas:** `ParkingLot.findVehicles { ... }`
- **Classes and objects:** `ParkingLot` class; `TicketCounter` object
- **Inheritance:** `Car`, `Motorcycle`, `Truck` extend `Vehicle`
- **Interfaces and polymorphism:** `Billable` interface; the parking fee depends on the vehicle type
- **Data class:** `ParkingRecord`
- **Sealed class:** `ParkingResult`, handled with `when` in `Main.kt`
- **Suspend function and coroutine:** `scanPlate()`, launched for every vehicle in `main()`
