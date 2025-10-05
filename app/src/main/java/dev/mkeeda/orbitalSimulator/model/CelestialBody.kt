package dev.mkeeda.orbitalSimulator.model

import androidx.compose.ui.graphics.Color
import kotlin.math.*
import kotlin.random.Random

data class CelestialBody(
    val name: String = "Body",
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val mass: Float = 1.0f,
    val radius: Float = 20f,
    val color: Color = Color.Blue,
    val density: Float = 1.0f, // g/cm³相当の密度（ロシュ限界計算用）
    val isDebris: Boolean = false // デブリは崩壊しない
) {
    fun updatePosition(deltaTime: Float): CelestialBody {
        return copy(
            x = x + velocityX * deltaTime,
            y = y + velocityY * deltaTime
        )
    }

    fun applyForce(fx: Float, fy: Float, deltaTime: Float): CelestialBody {
        // 加速度を計算 a = f * m
        // 加速度から速度vを計算 v = a * t
        return copy(
            velocityX = velocityX + (fx / mass) * deltaTime,
            velocityY = velocityY + (fy / mass) * deltaTime
        )
    }

    fun distanceTo(other: CelestialBody): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx * dx + dy * dy)
    }

    fun relativeVelocityTo(other: CelestialBody): Float {
        val relativeVx = velocityX - other.velocityX
        val relativeVy = velocityY - other.velocityY
        return sqrt(relativeVx * relativeVx + relativeVy * relativeVy)
    }

    fun canCollapseWith(other: CelestialBody): Boolean {
        // デブリは崩壊しない
        if (isDebris || other.isDebris) return false

        // ロシュ限界距離を計算
        val rocheDistance = calculateRocheDistance(other)
        val distance = distanceTo(other)

        // 距離がロシュ限界内で、相対速度が低い場合のみ崩壊
        return distance < rocheDistance && relativeVelocityTo(other) < 100.0f
    }

    fun calculateRocheDistance(other: CelestialBody): Float {
        val primary = if (mass > other.mass) this else other
        val satellite = if (mass > other.mass) other else this

        val densityRatio = primary.density / satellite.density
        val rocheCoefficient = 3.0f

        return rocheCoefficient * primary.radius * densityRatio.pow(1.0f/3.0f)
    }

    fun createDebrisFrom(primary: CelestialBody): List<CelestialBody> {
        val debrisCount = 10
        val debris = mutableListOf<CelestialBody>()

        // 主天体からの方向ベクトルを計算
        val dx = x - primary.x
        val dy = y - primary.y
        val distance = distanceTo(primary)

        // 正規化された逃避方向
        val escapeX = if (distance > 0) dx / distance else 1.0f
        val escapeY = if (distance > 0) dy / distance else 0.0f

        // 各デブリの質量と大きさ
        val debrisMass = mass / debrisCount
        val debrisRadius = radius * 0.4f

        repeat(debrisCount) { i ->
            // 円周上にデブリを配置するための角度
            val angle = (2 * PI * i) / debrisCount
            val spreadRadius = radius * 0.8f

            // デブリの初期位置
            val debrisX = x + cos(angle).toFloat() * spreadRadius
            val debrisY = y + sin(angle).toFloat() * spreadRadius

            // デブリの速度を計算
            val baseSpeed = Random.nextDouble(20.0, 50.0).toFloat()
            val escapeComponent = Random.nextDouble(0.6, 1.0).toFloat()
            val randomAngle = Random.nextDouble(0.0, 2 * PI)
            val randomComponent = 1.0f - escapeComponent

            val newVelocityX = velocityX + baseSpeed * (
                escapeComponent * escapeX +
                randomComponent * cos(randomAngle).toFloat()
            )
            val newVelocityY = velocityY + baseSpeed * (
                escapeComponent * escapeY +
                randomComponent * sin(randomAngle).toFloat()
            )

            // デブリの色を生成
            val variation = (i * 0.1f) % 0.3f
            val dimming = 0.7f
            val debrisColor = Color(
                red = (color.red * dimming + variation).coerceIn(0f, 1f),
                green = (color.green * dimming + variation * 0.5f).coerceIn(0f, 1f),
                blue = (color.blue * dimming + variation * 0.3f).coerceIn(0f, 1f),
                alpha = 0.8f
            )

            debris.add(
                CelestialBody(
                    name = "${name}_Debris_${i + 1}",
                    x = debrisX,
                    y = debrisY,
                    velocityX = newVelocityX,
                    velocityY = newVelocityY,
                    mass = debrisMass,
                    radius = debrisRadius,
                    color = debrisColor,
                    density = density,
                    isDebris = true
                )
            )
        }

        return debris
    }
}