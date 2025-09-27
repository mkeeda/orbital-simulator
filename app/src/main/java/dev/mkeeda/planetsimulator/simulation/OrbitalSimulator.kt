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

    // 実際の太陽系データ（SI単位）
    // 太陽質量: 1.989 × 10^30 kg
    // 地球質量: 5.972 × 10^24 kg（太陽の約1/333,000）
    // 地球-太陽距離: 1.496 × 10^11 m（1天文単位）
    // 地球の公転速度: 約29.78 km/s

    // スケーリング設定
    // シミュレーション空間: 1000x1000単位
    // 距離スケール: 1AU = 200単位
    // 質量比は実際の比率を維持
    // 時間スケール: 1秒 = 約1日相当

    private val G = 40000.0 // 重力定数（スケーリング調整済み）

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
        // 実際の太陽系の質量比を使用
        val sunMass = 333000.0  // 太陽の質量（任意単位）
        val earthMass = 1.0     // 地球の質量を1として正規化
        val auDistance = 200.0  // 1天文単位 = 200シミュレーション単位

        // 円軌道速度の計算（ケプラーの第3法則に基づく）
        // 実際の地球の公転速度に相当する値に調整
        val earthOrbitalVelocity = sqrt(G * sunMass / auDistance)

        bodies = listOf(
            CelestialBody(
                id = 1,
                x = 500.0,
                y = 500.0,
                velocityX = 0.0,
                velocityY = 0.0,
                mass = sunMass,
                radius = 35f,  // 太陽の半径（視覚的に調整）
                color = Color(0xFFFDB813),  // 太陽の色
                name = "Sun"
            ),
            CelestialBody(
                id = 2,
                x = 500.0 + auDistance,
                y = 500.0,
                velocityX = 0.0,
                velocityY = earthOrbitalVelocity,
                mass = earthMass,
                radius = 10f,  // 地球の半径（視覚的に調整）
                color = Color(0xFF4169E1),  // 地球の青色
                name = "Earth"
            )
        )
    }
}