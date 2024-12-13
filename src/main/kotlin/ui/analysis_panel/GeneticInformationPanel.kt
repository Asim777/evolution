package ui.analysis_panel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.entity.Entity
import data.gene.containsNeuron
import data.gene.getNeuron
import domain.Simulation
import ui.*
import kotlin.math.cos
import kotlin.math.sin

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

    Column
    Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        // Entity Information
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .background(color = AppColors.AirForceBlue)
        ) {
            val entity = Simulation.selectedEntity.value ?: return
            EntityInformation(entity)
        }

        // Gene List
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .background(color = AppColors.AirForceBlue)
        ) {
            GeneList()
        }

        // Neuron Information
        //val neuron = Simulation.selectedNeuron.value.toUiModel() ?: return

        /*Box(
        modifier = Modifier
            .background(color = AppColors.AirForceBlue)
            .align(Alignment.BottomStart)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            InfoPanelHeader("Neuron information:")
            InfoPanelText("Name: ${neuron.name}")
            InfoPanelText("Explanation: ${neuron.explanation}")
            InfoPanelText("Excitement value: ${neuron.excitementValue}")
        }
    }*/
    }

    Row(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        val textMeasurer = rememberTextMeasurer()

        GeneNeuralMap(textMeasurer)
    }
}

@Composable
@OptIn(ExperimentalTextApi::class)
private fun GeneNeuralMap(textMeasurer: TextMeasurer) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(460.dp)
            .graphicsLayer()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    handleNeuronClick(offset)
                }
            }
            .drawWithCache {
                drawNeuralNetworkMap(textMeasurer)
            }
    ) {}
}

@Composable
private fun GeneList() {
    Column(modifier = Modifier.padding(8.dp)) {
        InfoPanelHeader("Genes:")
        LazyColumn {
            items(Simulation.selectedEntity.value?.genome?.genes ?: emptyArray()) { gene ->
                GeneListItem(
                    gene = gene,
                    isSelected = gene.name == Simulation.selectedGene.value?.name,
                    onClick = {
                        Simulation.selectedGene.value = gene
                    }
                )
            }
        }
    }
}

@Composable
private fun EntityInformation(entity: Entity) {
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            InfoPanelHeader("Entity information:")
        }

        Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp)) {
            Column {
                InfoPanelText("Age: ${entity.age}")
                InfoPanelText("Generation: ${entity.generation}")
                InfoPanelText("Mutation count: ${entity.ancestralMutationCount}")
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                InfoPanelText("Health: ${entity.health}")
                InfoPanelText("Energy: ${entity.energy}")
                InfoPanelText("Hunger: ${entity.hunger}")
                InfoPanelText("Mating Drive: ${entity.matingDrive}")
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun CacheDrawScope.drawNeuralNetworkMap(textMeasurer: TextMeasurer) =
    onDrawBehind {
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
            getUiSensorNeurons()/*.filter { it.isActive }*/,
            center,
            sensorRadius,
            AppColors.Finn,
            textMeasurer,
            shouldSpread = false
        )

        // Draw Inner Neurons
        drawNeurons(
            getUiInnerNeurons()/*.filter { it.isActive }*/,
            center,
            innerRadius,
            AppColors.MidnightGreen,
            textMeasurer,
            shouldSpread = false
        )

        // Draw Sink Neurons
        drawNeurons(
            getUiSinkNeurons()/*.filter { it.isActive }*/,
            center,
            sinkRadius,
            AppColors.AirForceBlue,
            textMeasurer,
            shouldSpread = false
        )

        // Draw connections
        drawConnections()
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

        val isNeuronInSelectedGene = Simulation.selectedGene.value?.containsNeuron(neuron.name) == true
        val isNeuronInSelectedGenome =
            Simulation.selectedEntity.value?.genome?.genes?.any { it.containsNeuron(neuron.name) } == true

        val modifiedColor =
            if (isNeuronInSelectedGene || (Simulation.selectedGene.value == null && isNeuronInSelectedGenome)) {
                color
            } else {
                Color.Gray
            }

        // Drawing fill circle
        drawCircle(
            color = modifiedColor,
            radius = 35f,
            center = offset
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
        neuronUiData[neuron.name]?.center = Offset(x = offset.x, y = offset.y)
    }
}

fun DrawScope.drawConnections() {
    Simulation.selectedEntity.value?.genome?.connections?.forEach { connection ->
        val inputNeuron = neuronUiData[connection.input.id]
        val outputNeuron = neuronUiData[connection.output.id]

        val inputCenter = inputNeuron?.center ?: return@forEach
        val outputCenter = outputNeuron?.center ?: return@forEach

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

        // Drawing a line between input and output Neurons
        val connectionIsPartOfSelectedGene = Simulation.selectedGene.value?.containsNeuron(inputNeuron.name) == true
                && Simulation.selectedGene.value?.containsNeuron(outputNeuron.name) == true
        val connectionColor =
            if (connectionIsPartOfSelectedGene || Simulation.selectedGene.value == null) AppColors.Raspberry else Color.LightGray

        val inputSelectionColor = getNeuronSelectionColor(inputNeuron)
        val outputSelectionColor = getNeuronSelectionColor(outputNeuron)

        drawLine(
            color = connectionColor,
            strokeWidth = 8f,
            start = trimmedStart,
            end = trimmedEnd
        )

        // Drawing selection circle around input circle to make the connection more visible
        drawCircle(
            color = inputSelectionColor,
            radius = 37f,
            center = inputCenter,
            style = Stroke(width = 8f)
        )

        // Drawing selection circle around output circle to make the connection more visible
        drawCircle(
            color = outputSelectionColor,
            radius = 37f,
            center = outputCenter,
            style = Stroke(width = 8f)
        )

        // Drawing Neuron selection circle around Neuron to make the selected Neuron visible
        if (Simulation.selectedNeuron.value?.id == inputNeuron.name) {
            drawCircle(
                color = AppColors.FireEngineRed,
                radius = 39f,
                center = inputCenter,
            )
        }

        if (Simulation.selectedNeuron.value?.id == outputNeuron.name) {
            drawCircle(
                color = AppColors.FireEngineRed,
                radius = 39f,
                center = outputCenter,
                style = Stroke(width = 8f)
            )
        }
    }
}

private fun getNeuronSelectionColor(neuron: NeuronUiModel): Color {
    val isNeuronSelected = Simulation.selectedNeuron.value?.id == neuron.name
    val neuronSelectionColor = if (Simulation.selectedGene.value?.containsNeuron(neuron.name) == true) {
        if (isNeuronSelected) Color.Yellow else AppColors.Raspberry
    } else {
        if (Simulation.selectedGene.value != null) Color.LightGray else AppColors.Raspberry
    }
    return neuronSelectionColor
}

fun handleNeuronClick(offset: Offset) {
    neuronUiData.forEach { (name, neuron) ->
        val center = neuron.center ?: return@forEach
        val radius = 35f

        if ((offset - center).getDistance() <= radius) {
            // Neuron circle was clicked
            Simulation.selectedNeuron.value = Simulation.selectedGene.value?.getNeuron(neuron.name) ?: return@forEach
        }
    }
}

@Composable
fun InfoPanelText(text: String) =
    Text(
        text = text,
        fontSize = 10.sp,
        color = Color.White
    )

@Composable
fun InfoPanelHeader(text: String) =
    Text(
        modifier = Modifier.padding(bottom = 6.dp),
        text = text,
        fontSize = 14.sp, color = AppColors.TimberWorld
    )

fun offsetAt(center: Offset, index: Int, total: Int, radius: Float): Offset {
    val angle = 2 * Math.PI * index / total
    val x = center.x + radius * 1.18f * cos(angle).toFloat()
    val y = center.y + radius * sin(angle).toFloat()
    return Offset(x, y)
}