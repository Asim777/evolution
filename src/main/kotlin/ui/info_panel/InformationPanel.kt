package ui.info_panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.Simulation
import ui.AppColors

@Composable
fun InformationPanel() {
    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .background(AppColors.AirForceBlue)
            .fillMaxWidth()
    ) {
        // Left column
        Column(
            Modifier.weight(0.5f, true)
                .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Text(color = AppColors.Beige, text = "Time elapsed: ${Simulation.timeElapsed.value}")
            Text(color = AppColors.Beige, text = "Population: ${Simulation.population.value}")
            Text(color = AppColors.Beige, text = "Food: ${Simulation.numberOfFood.value}")
            Text(color = AppColors.Beige, text = "Species: ${Simulation.numberOfSpecies.value}")
        }
        // Right column
        Column(
            Modifier.weight(0.5f, true)
                .padding(end = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Text(color = AppColors.Beige, text = "Simulation speed: ${Simulation.simulationSpeed.value}")
        }
    }
}