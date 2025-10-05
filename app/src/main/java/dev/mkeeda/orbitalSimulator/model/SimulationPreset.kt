package dev.mkeeda.orbitalSimulator.model

data class SimulationPreset(
    val name: String,
    val description: String,
    val gravityConstant: Float,
    val bodies: List<CelestialBody>
)