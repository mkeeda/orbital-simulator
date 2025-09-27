package dev.mkeeda.planetsimulator.model

data class SimulationPreset(
    val name: String,
    val description: String,
    val gravityConstant: Double,
    val bodies: List<CelestialBody>
)