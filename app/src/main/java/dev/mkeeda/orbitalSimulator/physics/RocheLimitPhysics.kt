package dev.mkeeda.orbitalSimulator.physics

import dev.mkeeda.orbitalSimulator.model.CelestialBody

/**
 * ロシュ限界の物理計算を統合したユーティリティ
 */
object RocheLimitPhysics {

    /**
     * 2つの天体間でロシュ限界による崩壊処理を実行
     * @return 結果として残る天体と新たに生成されたデブリのペア
     */
    fun processRocheLimit(body1: CelestialBody, body2: CelestialBody): Pair<List<CelestialBody>, List<CelestialBody>> {
        // どちらかがデブリの場合は処理しない
        if (body1.isDebris || body2.isDebris) {
            return Pair(listOf(body1, body2), emptyList())
        }

        // 崩壊可能かチェック
        if (!body1.canCollapseWith(body2)) {
            return Pair(listOf(body1, body2), emptyList())
        }

        // 質量で主天体と崩壊天体を決定
        val (primary, victim) = if (body1.mass > body2.mass) {
            Pair(body1, body2)
        } else {
            Pair(body2, body1)
        }

        // デブリを生成
        val debris = victim.createDebrisFrom(primary)

        // 主天体は残し、崩壊天体の代わりにデブリを返す
        return Pair(listOf(primary), debris)
    }

    /**
     * 天体リストから崩壊処理を実行
     * @return 処理後の天体リスト
     */
    fun processCollisions(bodies: List<CelestialBody>): List<CelestialBody> {
        val survivingBodies = mutableListOf<CelestialBody>()
        val processedIndices = mutableSetOf<Int>()
        val newDebris = mutableListOf<CelestialBody>()

        for (i in bodies.indices) {
            if (i in processedIndices) continue

            var currentBody = bodies[i]
            var collapsed = false

            for (j in bodies.indices) {
                if (i == j || j in processedIndices) continue

                val otherBody = bodies[j]

                if (currentBody.canCollapseWith(otherBody)) {
                    val (survivors, debris) = processRocheLimit(currentBody, otherBody)

                    // 主天体だけを残す
                    if (survivors.size == 1) {
                        currentBody = survivors[0]
                        processedIndices.add(if (currentBody === bodies[i]) j else i)
                        newDebris.addAll(debris)
                        collapsed = true
                    }
                }
            }

            if (!collapsed || currentBody === bodies[i]) {
                survivingBodies.add(currentBody)
            }
        }

        survivingBodies.addAll(newDebris)
        return survivingBodies
    }
}