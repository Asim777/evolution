package ui.controls

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import data.SimulationParams
import domain.Simulation
import domain.SimulationSpeed
import ui.AppColors

@Composable
fun SimulationControls(simulationParams: SimulationParams) {
    val buttonColors = ButtonDefaults.buttonColors(
        backgroundColor = AppColors.MidnightGreen,
        contentColor = AppColors.Beige
    )

    Row(
        modifier = Modifier
            .padding(top = 40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        // Setup
        val setupIcon = painterResource("drawables/setup.svg")

        Button(
            modifier = Modifier.size(48.dp),
            colors = buttonColors,
            onClick = {
                Simulation.setup(simulationParams)
            }
        ) {
            Icon(
                painter = setupIcon,
                contentDescription = "Setup the Simulation",
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Speed down
        val speedDownIcon = painterResource("drawables/speed_down.svg")
        Button(
            modifier = Modifier.size(48.dp, 48.dp),
            colors = buttonColors,
            onClick = {}
        ) {
            Icon(
                painter = speedDownIcon,
                contentDescription = "Speed down the Simulation",
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Run
        val runIcon = painterResource("drawables/run.svg")
        Button(
            modifier = Modifier.size(48.dp, 48.dp),
            colors = buttonColors,
            onClick = {
                Simulation.setup(simulationParams)
                Simulation.run()
            }
        ) {
            Icon(
                painter = runIcon,
                contentDescription = "Run the Simulation",
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Speed up
        val speedUpIcon = painterResource("drawables/speed_up.svg")
        Button(
            modifier = Modifier.size(48.dp, 48.dp),
            colors = buttonColors,
            onClick = {
                Simulation.setSpeed(SimulationSpeed.Double)
            }
        ) {
            Icon(
                painter = speedUpIcon,
                contentDescription = "Speed up the Simulation",
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Sample
        val sampleIcon = painterResource("drawables/sample.svg")
        Button(
            modifier = Modifier.size(48.dp, 48.dp),
            colors = buttonColors,
            onClick = {
                Simulation.onSampleClicked()
            }
        ) {
            Icon(
                painter = sampleIcon,
                contentDescription = "Sample the Entities",
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Statistics
        val statisticsIcon = painterResource("drawables/statistics.svg")
        Button(
            modifier = Modifier.size(48.dp, 48.dp),
            colors = buttonColors,
            onClick = {}
        ) {
            Icon(
                painter = statisticsIcon,
                contentDescription = "Show the Statistics",
            )
        }

        Spacer(modifier = Modifier.weight(1.0f))

        // Select random Entity
        val selectEntityIcon = painterResource("drawables/select.svg")
        Button(
            modifier = Modifier.size(48.dp, 48.dp),
            colors = buttonColors,
            onClick = {}
        ) {
            Icon(
                painter = selectEntityIcon,
                contentDescription = "Select a random Entity",
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Add Entity to Observation
        val addToObservationIcon = painterResource("drawables/add_to_observation.svg")
        Button(
            modifier = Modifier.size(48.dp, 48.dp),
            colors = buttonColors,
            onClick = {}
        ) {
            Icon(
                painter = addToObservationIcon,
                contentDescription = "Add selected Entity to Observation",
            )
        }
    }
}