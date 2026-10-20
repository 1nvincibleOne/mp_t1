package com.example.parkinglot.ui

import androidx.lifecycle.ViewModel
import com.example.parkinglot.model.Car
import com.example.parkinglot.model.Motorcycle
import com.example.parkinglot.model.ParkingLot
import com.example.parkinglot.model.ParkingResult
import com.example.parkinglot.model.Truck
import com.example.parkinglot.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val vehicleTypes = listOf("Car", "Motorcycle", "Truck")

data class ParkingUiState(
    val parked: Map<Int, Vehicle> = emptyMap(),
    val occupied: Int = 0,
    val capacity: Int = 0,
    val revenue: Int = 0,
    val filter: String = "All",
    val message: String? = null,
    val hours: Map<String, Int> = emptyMap(), // plate -> hours, default 1
)

class ParkingViewModel : ViewModel() {
    private val lot = ParkingLot(capacity = 6)

    private val _uiState = MutableStateFlow(ParkingUiState(capacity = lot.capacity))
    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    init {
        lot.park(Car("123ABC01", "Aliya"))
        lot.park(Motorcycle("777MOT02", "Daniyar"))
        lot.park(Truck("555TRK02", "Nurlan"))
        refresh()
    }

    // true if parked, so the form can clear
    fun park(plate: String, owner: String, type: String): Boolean {
        val cleanPlate = plate.trim().uppercase()
        val cleanOwner = owner.trim()
        if (cleanPlate.isEmpty() || cleanOwner.isEmpty()) {
            refresh("Enter a plate number and an owner")
            return false
        }

        val vehicle = when (type) {
            "Motorcycle" -> Motorcycle(cleanPlate, cleanOwner)
            "Truck" -> Truck(cleanPlate, cleanOwner)
            else -> Car(cleanPlate, cleanOwner)
        }
        val result = lot.park(vehicle)
        val message = when (result) {
            is ParkingResult.Parked -> "${vehicle.plate} parked at spot ${result.spot}, ticket #${result.ticket}"
            is ParkingResult.AlreadyParked -> "${result.plate} is already inside"
            ParkingResult.LotFull -> "No free spots for ${vehicle.plate}"
        }
        refresh(message)
        return result is ParkingResult.Parked
    }

    fun setHours(plate: String, hours: Int) {
        if (hours < 1) return
        _uiState.update { it.copy(hours = it.hours + (plate to hours)) }
    }

    fun leave(plate: String) {
        val record = lot.leave(plate, _uiState.value.hours[plate] ?: 1)
        _uiState.update { it.copy(hours = it.hours - plate) }
        refresh(record?.let { "${it.plate} stayed ${it.hours} h and paid ${it.fee} KZT" })
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(filter = filter) }
        refresh(_uiState.value.message)
    }

    private fun refresh(message: String? = null) {
        _uiState.update { state ->
            state.copy(
                parked = lot.findVehicles { state.filter == "All" || it.type == state.filter },
                occupied = lot.occupied,
                revenue = lot.totalRevenue(),
                message = message,
            )
        }
    }
}
