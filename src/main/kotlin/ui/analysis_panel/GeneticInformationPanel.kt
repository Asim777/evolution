package ui.analysis_panel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.gene.Genome
import domain.Simulation
import offsetAt
import ui.*

val neuronUiData: HashMap<String, NeuronUiModel> =
    HashMap<String, NeuronUiModel>().apply {
        putAll(getUiSensorNeurons().associateBy { it.name })
        putAll(getUiInnerNeurons().associateBy { it.name })
        putAll(getUiSinkNeurons().associateBy { it.name })
    }

@OptIn(ExperimentalTextApi::class)
@Composable
fun GeneticInformationPanel() {
    if (Simulation.selectedEntity.value == null) return

    val textMeasurer = rememberTextMeasurer()

    Text(
        text = "Genetic information:",
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        fontSize = 18.sp,
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Border around the canvas
        drawRect(
            color = AppColors.TimberWorld,
            size = size,
            style = Stroke(width = 4.dp.toPx())
        )

        val center = Offset(size.width / 2, size.height / 2)

        // Define Radii for the concentric circles
        val sensorRadius = size.minDimension / 2 * 0.8f
        val innerRadius = sensorRadius * 0.7f
        val sinkRadius = sensorRadius * 0.4f

        // Draw Sensor Neurons
        drawNeurons(
            getUiSensorNeurons(),
            center,
            sensorRadius,
            AppColors.Finn,
            textMeasurer,
            shouldSpread = true
        )

        // Draw Inner Neurons
        drawNeurons(
            getUiInnerNeurons(),
            center,
            innerRadius,
            AppColors.MidnightGreen,
            textMeasurer,
            shouldSpread = false
        )

        // Draw Sink Neurons
        drawNeurons(
            getUiSinkNeurons(),
            center,
            sinkRadius,
            AppColors.AirForceBlue,
            textMeasurer,
            shouldSpread = false
        )

        // Draw connections
        drawConnections()
    }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawNeurons(
    neurons: List<NeuronUiModel>,
    center: Offset,
    concentricCircleRadius: Float,
    color: Color,
    textMeasurer: TextMeasurer,
    shouldSpread: Boolean
) {
    neurons.forEachIndexed { index, neuron ->
        val modifiedRadius = if (shouldSpread) {
            if (index % 2 == 0) concentricCircleRadius * 1.1f else concentricCircleRadius
        } else concentricCircleRadius
        val offset = offsetAt(center, index, neurons.size, modifiedRadius)

        val modifiedColor = if (neuron.isActive) color else Color.Gray

        // Drawing fill circle
        drawCircle(
            color = modifiedColor,
            radius = 35f,
            center = offset,
        )

        val textLayoutResult: TextLayoutResult = textMeasurer.measure(
            text = AnnotatedString(neuron.name),
            style = TextStyle(
                color = AppColors.Beige,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        )

        drawText(
            textLayoutResult,
            topLeft = Offset(
                offset.x - textLayoutResult.size.width / 2,
                offset.y - textLayoutResult.size.height / 2
            )
        )

        // Update the mutable state for this Neuron with Neuron circle center coordinates
        neuronUiData[neuron.name]?.center = NeuronCoordinateUiModel(x = offset.x, y = offset.y)
    }
}

fun DrawScope.drawConnections() {
    val genome = Simulation.selectedEntity.value?.genome ?: return

    genome.connections.forEach { connection ->

        val inputNeuron = neuronUiData[connection.input.id]
        val outputNeuron = neuronUiData[connection.output.id]

        val inputCoordinates = inputNeuron?.center ?: return@forEach
        val outputCoordinates = outputNeuron?.center ?: return@forEach
        val inputCenter = Offset(inputCoordinates.x, inputCoordinates.y)
        val outputCenter = Offset(outputCoordinates.x, outputCoordinates.y)

        // Drawing connection line between two Neurons
        val trimAmountPx = 20 * density

        // Calculate direction vector
        val lineVector = inputCenter - outputCenter
        val lineLength = lineVector.getDistance()

        // Normalize the direction vector
        val directionVector = lineVector / lineLength

        // Calculate new start and end points by trimming
        val trimmedStart = inputCenter - directionVector * trimAmountPx
        val trimmedEnd = outputCenter + directionVector * trimAmountPx

        drawLine(
            color = AppColors.Raspberry,
            strokeWidth = 8f,
            start = trimmedStart,
            end = trimmedEnd
        )

        // Drawing selection circle around neuron circles to make the connection more visible
        drawCircle(
            color = AppColors.Raspberry,
            radius = 37f,
            center = inputCenter,
            style = Stroke(width = 8f)
        )

        drawCircle(
            color = AppColors.Raspberry,
            radius = 37f,
            center = outputCenter,
            style = Stroke(width = 8f)
        )
    }
}
