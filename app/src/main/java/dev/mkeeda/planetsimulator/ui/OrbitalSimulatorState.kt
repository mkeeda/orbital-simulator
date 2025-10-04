package dev.mkeeda.planetsimulator.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.mkeeda.planetsimulator.data.Preset
import dev.mkeeda.planetsimulator.model.CelestialBody
import dev.mkeeda.planetsimulator.model.SimulationPreset
import dev.mkeeda.planetsimulator.physics.RocheLimitPhysics
import kotlin.math.sqrt

class OrbitalSimulatorState {
    var bodies by mutableStateOf(listOf<CelestialBody>())
        private set

    var currentPreset by mutableStateOf(Preset.realSunAndEarth())
        private set

    var isRocheLimitEnabled by mutableStateOf(true)
        private set


    private var gravityConstant = 40000.0

    init {
        loadPreset(currentPreset)
    }

    fun update(deltaTime: Double) {
        // ロシュ限界チェックと崩壊処理
        if (isRocheLimitEnabled) {
            checkRocheLimitAndCollapse()
        }

        // 各天体に働く重力を計算
        val updatedBodies = mutableListOf<CelestialBody>()

        for (i in bodies.indices) {

            var fx = 0.0
            var fy = 0.0

            // 他のすべての天体からの重力を計算
            for (j in bodies.indices) {
                if (i != j) {
                    val dx = bodies[j].x - bodies[i].x
                    val dy = bodies[j].y - bodies[i].y
                    val distance = sqrt(dx * dx + dy * dy)

                    // 万有引力の法則: F = G * m1 * m2 / r^2
                    val force = gravityConstant * bodies[i].mass * bodies[j].mass / (distance * distance)
                    // 力の方向成分
                    fx += force * dx / distance
                    fy += force * dy / distance
                }
            }

            // 力を適用して速度を更新
            updatedBodies.add(bodies[i].applyForce(fx, fy, deltaTime))
        }

        // 位置を更新
        bodies = updatedBodies.map { it.updatePosition(deltaTime) }
    }

    fun loadPreset(preset: SimulationPreset) {
        currentPreset = preset
        gravityConstant = preset.gravityConstant
        bodies = preset.bodies
    }

    fun reset() {
        loadPreset(currentPreset)
    }

    fun toggleRocheLimit() {
        isRocheLimitEnabled = !isRocheLimitEnabled
    }

    /**
     * ロシュ限界チェックと崩壊処理
     */
    private fun checkRocheLimitAndCollapse() {
        bodies = RocheLimitPhysics.processCollisions(bodies)
    }
}