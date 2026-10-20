package com.example.parkinglot.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.parkinglot.model.Vehicle

@Composable
fun StatsHeader(free: Int, capacity: Int, revenue: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            StatItem(label = "Free spots", value = "$free / $capacity", modifier = Modifier.weight(1f))
            StatItem(label = "Revenue", value = "$revenue KZT", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ChipSelector(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option) },
            )
        }
    }
}

@Composable
fun ParkForm(onPark: (plate: String, owner: String, type: String) -> Boolean) {
    // Survives rotation
    var plate by rememberSaveable { mutableStateOf("") }
    var owner by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf(vehicleTypes.first()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Park a vehicle", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text("Plate number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = owner,
                onValueChange = { owner = it },
                label = { Text("Owner") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            ChipSelector(options = vehicleTypes, selected = type, onSelect = { type = it })
            Button(
                onClick = {
                    if (onPark(plate, owner, type)) {
                        plate = ""
                        owner = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Park")
            }
        }
    }
}

@Composable
fun VehicleCard(
    spot: Int,
    vehicle: Vehicle,
    hours: Int,
    onHoursChange: (Int) -> Unit,
    onLeave: () -> Unit,
) {
    // Resets on rotation and when filtered out
    var showFees by remember { mutableStateOf(false) }

    Card(onClick = { showFees = !showFees }, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VehicleIcon(vehicle = vehicle, spot = spot)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(vehicle.plate, style = MaterialTheme.typography.titleMedium)
                    Text("${vehicle.type} · ${vehicle.owner}")
                    Text("${vehicle.hourlyRate()} KZT/h", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (showFees) {
                Text(
                    (1..4).joinToString(" · ") { "$it h: ${vehicle.fee(it)}" } + " KZT",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onHoursChange(hours - 1) }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Fewer hours")
                }
                Text("$hours h")
                IconButton(onClick = { onHoursChange(hours + 1) }) {
                    Icon(Icons.Filled.Add, contentDescription = "More hours")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onLeave) {
                    Text("Leave · ${vehicle.fee(hours)} KZT")
                }
            }
        }
    }
}

@Composable
fun VehicleIcon(vehicle: Vehicle, spot: Int) {
    val icon: ImageVector = when (vehicle.type) {
        "Motorcycle" -> Icons.Filled.TwoWheeler
        "Truck" -> Icons.Filled.LocalShipping
        else -> Icons.Filled.DirectionsCar
    }
    Box {
        Image(
            imageVector = icon,
            contentDescription = vehicle.type,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .padding(10.dp),
        )
        Badge(modifier = Modifier.align(Alignment.BottomEnd)) {
            Text("$spot")
        }
    }
}
