import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.NumberOfNeurons
import data.SimulationParams
import data.neuron.getClippedNumberOfNeurons
import domain.Simulation
import ui.AppColors
import ui.analysis_panel.EntitiesList
import ui.analysis_panel.GeneticInformationPanel
import ui.controls.SimulationControls
import ui.info_panel.InformationPanel
import ui.world.World
import kotlin.math.pow

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "EvolveIt",
        icon = painterResource("drawables/app_icon.png"),
        state = rememberWindowState(width = 1920.dp, height = 1080.dp)
    ) {

        val contextMenuRepresentation = if (isSystemInDarkTheme()) {
            DarkDefaultContextMenuRepresentation
        } else {
            LightDefaultContextMenuRepresentation
        }

        MaterialTheme {
            CompositionLocalProvider(LocalContextMenuRepresentation provides contextMenuRepresentation) {
                Surface(
                    Modifier.fillMaxSize().background(AppColors.Beige),
                ) {
                    app()
                }
            }
        }
    }
}

@Composable
@Preview
fun app() {
    var simulationParams by remember {
        mutableStateOf(
            Simulation.getSimulationParams()
        )
    }

    fun updateNumberOfNeurons(numberOfNeurons: NumberOfNeurons) {
        simulationParams = simulationParams.copy(
            numberOfNeurons = numberOfNeurons
        )
    }

    fun onWorldSizeChanged(input: String) {
        // We accept numerical values only
        if (input.matches(Regex("^\\d+\$"))) {
            val number = input.toInt()
            // We constrain World size between 100 and 1.000.000
            if (number in 100..1000000) {
                simulationParams = simulationParams.copy(worldSize = input.toInt())
            }
        }
    }

    fun onInitialPopulationChanged(input: String) {
        // We accept numerical values only
        if (input.matches(Regex("^\\d+\$"))) {
            val number = input.toInt()
            // We constrain Initial population between 10 and half of cells in the world
            // (world size * world size) / 2
            val maxInitialPopulation = simulationParams.initialPopulation.toDouble().pow(2.0) / 2
            if (number in 10..maxInitialPopulation.toInt()) {
                simulationParams = simulationParams.copy(initialPopulation = number)
            }
        }
    }

    fun onGenomeLengthChanged(input: String) {
        // We accept numerical values only
        if (input.matches(Regex("^\\d+\$"))) {
            val number = input.toInt()
            // We constrain Genome length between 1 and 100
            if (number in 1..100) {
                simulationParams = simulationParams.copy(genomeLength = number)
            }
        }
    }

    fun onFoodAvailabilityChanged(input: String) {
        // We accept float values with up to 2 decimal points between 0 and 1 including 0 and 1
        if (input.matches(Regex("^(0(\\.\\d{1,2})?|1(\\.0{1,2})?)\$"))) {
            val number = input.toFloat()
            simulationParams = simulationParams.copy(foodAvailability = number)
        }
    }

    fun onMutationRateChanged(input: String) {
        // We accept float values with up to 2 decimal points between 0 and 1 including 0 and 1
        if (input.matches(Regex("^(0(\\.\\d{1,2})?|1(\\.0{1,2})?)\$"))) {
            val number = input.toFloat()
            simulationParams = simulationParams.copy(mutationRate = number)
        }
    }

    fun onNumberOfNeuronsChanged() {
        // We have 7 combinations of sensor, inner and sink neurons, and we choose one of them depending on the proximity of
        // the total number of neurons chosen to total number of neurons in combinations
        updateNumberOfNeurons(
            getClippedNumberOfNeurons(simulationParams.numberOfNeurons.total)
        )
    }

    fun getNumberOfNeuronsText(simulationParams: SimulationParams) =
        simulationParams.numberOfNeurons.run {
            "Number of neurons: $total\nSensor: $sensorNeurons  Inner: $innerNeurons  Sink: $sinkNeurons"
        }

    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Start new simulation") {

                },
                ContextMenuItem("Sample") {

                },
                ContextMenuItem("Speed up") {

                },
                ContextMenuItem("Speed down") {

                },
                ContextMenuItem("Select random Entity") {

                },
                ContextMenuItem("Add to observation") {

                }
            )
        }
    ) {
        Scaffold {
            println("Main.kt app after Scaffold")
            // Top pane with World, Controls, Simulation info and Analysis panel/Statistics panel
            Column {
                Row(modifier = Modifier.height(900.dp)) {
                    // Left column containing World and Controls
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(24.dp)
                    ) {
                        // World
                        World()
                    }

                    // Right column containing Information panel, Analysis panel and Statistics panel
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(24.dp)
                    ) {
                        // Simulation Information panel
                        Row(modifier = Modifier.padding(top = 20.dp)) {
                            InformationPanel()
                        }

                        // Analysis panel
                        Row(modifier = Modifier.padding(top = 20.dp)) {
                            // Entities List
                            Column(modifier = Modifier.fillMaxWidth(1f).fillMaxHeight()) {
                                EntitiesList()
                            }

                            // Genetic Information Panel
                            Column(modifier = Modifier.fillMaxWidth(1.8f).fillMaxHeight()) {
                                GeneticInformationPanel()
                            }
                        }
                    }
                }

                // Simulation Controls
                Row(modifier = Modifier.height(50.dp).padding(start = 24.dp, end = 24.dp)) {
                    SimulationControls(simulationParams)
                }

                /*// Console
                Row(modifier = Modifier.height(200.dp)) {
                  // Controls
                }*/
                // World Setup input parameters
                /*  Column(
              modifier = Modifier
                  .width(400.dp)
                  .padding(24.dp),
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.Top
          ) {
              // World size TextField
              OutlinedTextField(
                  modifier = Modifier
                      .size(200.dp, 60.dp)
                      .padding(0.dp),
                  value = simulationParams.value.worldSize.toString(),
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
                  value = simulationParams.value.initialPopulation.toString(),
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
                  value = simulationParams.value.genomeLength.toString(),
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
                  value = simulationParams.value.foodAvailability.toString(),
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
                  value = simulationParams.value.mutationRate.toString(),
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
                  value = simulationParams.value.numberOfNeurons.total.toFloat(),
                  onValueChange = { value ->
                      updateNumberOfNeurons(
                          simulationParams.value.numberOfNeurons.copy(
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
                  text = getNumberOfNeuronsText(simulationParams.value)
              )
              Spacer(modifier = Modifier.height(10.dp))
          }*/
            }


        }
    }
}