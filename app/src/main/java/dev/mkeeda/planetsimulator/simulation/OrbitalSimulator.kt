package dev.mkeeda.planetsimulator.simulation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import dev.mkeeda.planetsimulator.model.CelestialBody
import kotlin.math.sqrt

class OrbitalSimulator {
    var bodies by mutableStateOf(listOf<CelestialBody>())
        private set

    private val G = 50000.0 // 重力定数（シミュレーション用に調整）

    init {
        reset()
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
                        val force = G * bodies[i].mass * bodies[j].mass / (distance * distance)
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

    fun reset() {
        val sunMass = 100.0
        val planetMass = 0.1
        val distance = 200.0

        // 円軌道に必要な速度を計算: v = sqrt(G * M / r)
        val orbitalVelocity = sqrt(G * sunMass / distance)

        bodies = listOf(
            CelestialBody(
                id = 1,
                x = 500.0,
                y = 500.0,
                velocityX = 0.0,
                velocityY = 0.0,
                mass = sunMass,
                radius = 30f,
                color = Color.Yellow,
                name = "Sun"
            ),
            CelestialBody(
                id = 2,
                x = 500.0 + distance,
                y = 500.0,
                velocityX = 0.0,
                velocityY = orbitalVelocity,
                mass = planetMass,
                radius = 15f,
                color = Color.Blue,
                name = "Planet"
            )
        )
    }
}