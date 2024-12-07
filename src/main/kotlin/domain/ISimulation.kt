package domain

import data.SimulationParams
import data.entity.Entity
import data.gene.Genome

interface ISimulation {
    fun setup(simulationParams: SimulationParams) : Unit
    fun run() : Unit
    fun setFoodAvailability(foodAvailability: Float)
    fun setMutationRate(mutationRate: Float)
    fun setSpeed(speed: SimulationSpeed)
    fun onSampleClicked()

    fun getGenePool() : List<Genome>
    fun getSimulationParams() : SimulationParams
    fun getEntities() : List<Entity>
}