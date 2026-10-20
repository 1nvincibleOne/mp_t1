package com.example.parkinglot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen(viewModel: ParkingViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("City Parking") }) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                StatsHeader(
                    free = state.capacity - state.occupied,
                    capacity = state.capacity,
                    revenue = state.revenue,
                )
            }
            item { ParkForm(onPark = viewModel::park) }
            state.message?.let { message ->
                item { Text(message, color = MaterialTheme.colorScheme.primary) }
            }
            item {
                ChipSelector(
                    options = listOf("All") + vehicleTypes,
                    selected = state.filter,
                    onSelect = viewModel::setFilter,
                )
            }
            if (state.parked.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("No vehicles here")
                    }
                }
            }
            items(state.parked.entries.toList(), key = { it.value.plate }) { (spot, vehicle) ->
                VehicleCard(
                    spot = spot,
                    vehicle = vehicle,
                    hours = state.hours[vehicle.plate] ?: 1,
                    onHoursChange = { viewModel.setHours(vehicle.plate, it) },
                    onLeave = { viewModel.leave(vehicle.plate) },
                )
            }
        }
    }
}
