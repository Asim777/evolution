import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.NumberOfNeurons
import data.WorldParams
import data.neuron.Neuron
import data.neuron.getClippedNumberOfNeurons
import data.neuron.getNeuronDistributionByCategory
import java.awt.Dimension
import java.lang.IllegalArgumentException
import kotlin.math.pow

fun main() = application {
    //val icon = painterResource("sample.png")

    Window(
        onCloseRequest = ::exitApplication,
        title = "EvolveIt",
        //icon = icon
    ) {
        window.size = Dimension(1300, 1000)

        val contextMenuRepresentation = if (isSystemInDarkTheme()) {
            DarkDefaultContextMenuRepresentation
        } else {
            LightDefaultContextMenuRepresentation
        }

        MaterialTheme(
            colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
        ) {
            CompositionLocalProvider(LocalContextMenuRepresentation provides contextMenuRepresentation) {
                Surface(Modifier.fillMaxSize()) {
                    app()
                }
            }
        }
    }
}

@Composable
@Preview
fun app() {
    val worldParams = remember {
        mutableStateOf(
            WorldParams(
                worldSize = 1000,
                initialPopulation = 1000,
                genomeLength = 4,
                foodAvailability = 0.5f,
                mutationRate = 0.01f,
                NumberOfNeurons(
                    total = 9,
                    sensorNeurons = 4,
                    innerNeurons = 0,
                    sinkNeurons = 5
                )
            )
        )
    }

    fun updateNumberOfNeurons(numberOfNeurons: NumberOfNeurons) {
        worldParams.value = worldParams.value.copy(
            numberOfNeurons = numberOfNeurons
        )
    }

    fun onWorldSizeChanged(input: String) {
        // We accept numerical values only
        if (input.matches(Regex("^\\d+\$"))) {
            val number = input.toInt()
            // We constrain World size between 100 and 1.000.000
            if (number in 100..1000000) {
                worldParams.value = worldParams.value.copy(worldSize = input.toInt())
            }
        }
    }

    fun onInitialPopulationChanged(input: String) {
        // We accept numerical values only
        if (input.matches(Regex("^\\d+\$"))) {
            val number = input.toInt()
            // We constrain Initial population between 10 and half of cells in the world
            // (world size * world size) / 2
            val maxInitialPopulation = worldParams.value.initialPopulation.toDouble().pow(2.0) / 2
            if (number in 10..maxInitialPopulation.toInt()) {
                worldParams.value = worldParams.value.copy(initialPopulation = number)
            }
        }
    }

    fun onGenomeLengthChanged(input: String) {
        // We accept numerical values only
        if (input.matches(Regex("^\\d+\$"))) {
            val number = input.toInt()
            // We constrain Genome length between 1 and 100
            if (number in 1..100) {
                worldParams.value = worldParams.value.copy(genomeLength = number)
            }
        }
    }

    fun onFoodAvailabilityChanged(input: String) {
        // We accept float values with up to 2 decimal points between 0 and 1 including 0 and 1
        if (input.matches(Regex("^(0(\\.\\d{1,2})?|1(\\.0{1,2})?)\$"))) {
            val number = input.toFloat()
            worldParams.value = worldParams.value.copy(foodAvailability = number)
        }
    }

    fun onMutationRateChanged(input: String) {
        // We accept float values with up to 2 decimal points between 0 and 1 including 0 and 1
        if (input.matches(Regex("^(0(\\.\\d{1,2})?|1(\\.0{1,2})?)\$"))) {
            val number = input.toFloat()
            worldParams.value = worldParams.value.copy(mutationRate = number)
        }
    }

    fun onNumberOfNeuronsChanged() {
        // We have 7 combinations of sensor, inner and sink neurons, and we choose one of them depending on the proximity of
        // the total number of neurons chosen to total number of neurons in combinations
        updateNumberOfNeurons(
            getClippedNumberOfNeurons(worldParams.value.numberOfNeurons.total)
        )
    }

    fun getNumberOfNeuronsText(worldParams: WorldParams) =
        worldParams.numberOfNeurons.run {
            "Number of neurons: $total\nSensor: $sensorNeurons  Inner: $innerNeurons  Sink: $sinkNeurons"
        }

    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Start new simulation") {

                },
                ContextMenuItem("Speed up") {

                },
                ContextMenuItem("Speed down") {

                }
            )
        }
    ) {
        Scaffold {
            Row {
                // Left pane with World
                Column(
                    modifier = Modifier
                        .width(500.dp)
                        .padding(24.dp)
                ) {
                    Box {

                    }
                }
                // Right pane with input parameters and controls
                // World size TextField
                Column(
                    modifier = Modifier
                        .width(400.dp)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .size(200.dp, 60.dp)
                            .padding(0.dp),
                        value = worldParams.value.worldSize.toString(),
                        label = { Text("World size") },
                        onValueChange = { value ->
                            onWorldSizeChanged(value)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            color = White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Initial Population TextField
                    OutlinedTextField(
                        modifier = Modifier
                            .size(200.dp, 60.dp)
                            .padding(0.dp),
                        value = worldParams.value.initialPopulation.toString(),
                        label = { Text("Initial population") },
                        onValueChange = { value ->
                            onInitialPopulationChanged(value)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            color = White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Genome Length TextField
                    OutlinedTextField(
                        modifier = Modifier
                            .size(200.dp, 60.dp)
                            .padding(0.dp),
                        value = worldParams.value.genomeLength.toString(),
                        label = { Text("Genome length") },
                        onValueChange = { value ->
                            onGenomeLengthChanged(value)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            color = White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Food Availability TextField
                    OutlinedTextField(
                        modifier = Modifier
                            .size(200.dp, 60.dp)
                            .padding(0.dp),
                        value = worldParams.value.foodAvailability.toString(),
                        label = { Text("Food availability") },
                        onValueChange = { value ->
                            onFoodAvailabilityChanged(value)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            color = White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Mutation Rate TextField
                    OutlinedTextField(
                        modifier = Modifier
                            .size(200.dp, 60.dp)
                            .padding(0.dp),
                        value = worldParams.value.mutationRate.toString(),
                        label = { Text("Mutation rate") },
                        onValueChange = { value ->
                            onMutationRateChanged(value)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            color = White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Number of Neurons
                    Slider(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(0.dp),
                        value = worldParams.value.numberOfNeurons.total.toFloat(),
                        onValueChange = { value ->
                            updateNumberOfNeurons(
                                worldParams.value.numberOfNeurons.copy(
                                    total = value.toInt(),
                                )
                            )
                        },
                        onValueChangeFinished = {
                            onNumberOfNeuronsChanged()
                        },
                        enabled = true,
                        valueRange = 1.0f..53.0f,
                        colors = SliderDefaults.colors()
                    )
                    Text(
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start,
                        text = getNumberOfNeuronsText(worldParams.value)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Controls
                    Row(
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        // Setup
                        Button(
                            modifier = Modifier.size(80.dp, 40.dp),
                            onClick = {
                                Simulation(worldParams.value).setup()
                            }
                        ) {
                            Text("Setup")
                        }
                        Spacer(modifier = Modifier.width(20.dp))

                        //Start
                        Button(
                            modifier = Modifier.size(80.dp, 40.dp),
                            onClick = {
                                Simulation(worldParams.value).apply {
                                    setup()
                                    start()
                                }
                            }
                        ) {
                            Text("Run")
                        }
                    }
                }
            }
        }
    }
}