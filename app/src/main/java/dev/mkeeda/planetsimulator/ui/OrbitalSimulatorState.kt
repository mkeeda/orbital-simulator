package dev.mkeeda.planetsimulator.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.mkeeda.planetsimulator.data.Preset
import dev.mkeeda.planetsimulator.model.CelestialBody
import dev.mkeeda.planetsimulator.model.SimulationPreset
import dev.mkeeda.planetsimulator.model.Trail
import dev.mkeeda.planetsimulator.model.TrailPoint
import dev.mkeeda.planetsimulator.physics.RocheLimitPhysics
import kotlin.math.sqrt

class OrbitalSimulatorState {
    var bodies by mutableStateOf(listOf<CelestialBody>())
        private set

    var trails by mutableStateOf(mapOf<String, Trail>())
        private set

    var isTrailEnabled by mutableStateOf(true)
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

        // 軌跡を更新
        if (isTrailEnabled) {
            updateTrails()
        }
    }

    fun loadPreset(preset: SimulationPreset) {
        currentPreset = preset
        gravityConstant = preset.gravityConstant
        bodies = preset.bodies
        initializeTrails()
    }

    fun reset() {
        loadPreset(currentPreset)
    }

    fun toggleRocheLimit() {
        isRocheLimitEnabled = !isRocheLimitEnabled
    }

    fun toggleTrail() {
        isTrailEnabled = !isTrailEnabled
        if (!isTrailEnabled) {
            clearTrails()
        }
    }

    /**
     * ロシュ限界チェックと崩壊処理
     */
    private fun checkRocheLimitAndCollapse() {
        val previousBodies = bodies
        bodies = RocheLimitPhysics.processCollisions(bodies)

        // デブリが生成された場合は軌跡を削除
        if (bodies.size != previousBodies.size) {
            val currentBodyNames = bodies.map { it.name }.toSet()
            val removedNames = previousBodies.map { it.name }.filterNot { it in currentBodyNames }

            val updatedTrails = trails.toMutableMap()
            removedNames.forEach { updatedTrails.remove(it) }
            trails = updatedTrails
        }
    }

    private fun initializeTrails() {
        val newTrails = mutableMapOf<String, Trail>()
        bodies.forEach { body ->
            if (!body.isDebris) {  // デブリは軌跡を記録しない
                newTrails[body.name] = Trail(
                    bodyName = body.name,
                    positions = emptyList(),
                    color = body.color.copy(alpha = 0.5f)
                )
            }
        }
        trails = newTrails
    }

    private fun updateTrails() {
        val updatedTrails = trails.toMutableMap()

        bodies.forEach { body ->
            if (!body.isDebris) {  // デブリは軌跡を記録しない
                val trail = updatedTrails[body.name]
                if (trail != null) {
                    updatedTrails[body.name] = trail.addPosition(body.x, body.y)
                } else {
                    // 新しい天体の場合（ありえないが念のため）
                    updatedTrails[body.name] = Trail(
                        bodyName = body.name,
                        positions = listOf(TrailPoint(body.x, body.y)),
                        color = body.color.copy(alpha = 0.5f)
                    )
                }
            }
        }

        trails = updatedTrails
    }

    private fun clearTrails() {
        val clearedTrails = trails.mapValues { it.value.clear() }
        trails = clearedTrails
    }
}