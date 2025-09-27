package dev.mkeeda.planetsimulator.simulation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dev.mkeeda.planetsimulator.data.PresetManager
import dev.mkeeda.planetsimulator.model.CelestialBody
import dev.mkeeda.planetsimulator.model.SimulationPreset
import kotlin.math.sqrt

class OrbitalSimulator {
    var bodies by mutableStateOf(listOf<CelestialBody>())
        private set

    var currentPreset by mutableStateOf(PresetManager.getSunEarthPreset())
        private set

    private var gravityConstant = 40000.0

    init {
        loadPreset(currentPreset)
    }

    fun update(deltaTime: Double) {
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

                    // 距離が近すぎる場合の保護
                    // F = G * m1 * m2 / r² の式で r → 0 の時、F → ∞ となり
                    // 数値オーバーフローや天体が飛び散る問題を防ぐ
                    if (distance > 1.0) {
                        // 万有引力の法則: F = G * m1 * m2 / r^2
                        val force = gravityConstant * bodies[i].mass * bodies[j].mass / (distance * distance)
                        // 力の方向成分
                        fx += force * dx / distance
                        fy += force * dy / distance
                    }
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
}