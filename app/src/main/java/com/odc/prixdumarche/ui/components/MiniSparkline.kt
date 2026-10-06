package com.odc.prixdumarche.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Mini-graphique de tendance (sparkline) : forme uniquement, sans axe ni
 * légende — un badge Hausse/Baisse à côté reste la source de vérité textuelle
 * (la couleur seule ne doit jamais porter l'information).
 */
@Composable
fun MiniSparkline(
    points: List<Long>,
    couleur: Color,
    modifier: Modifier = Modifier
) {
    if (points.size < 3) return

    val min = points.min().toFloat()
    val max = points.max().toFloat()
    val plage = (max - min).takeIf { it > 0f } ?: 1f

    Canvas(modifier = modifier.width(56.dp).height(24.dp)) {
        val stepX = size.width / (points.size - 1)
        val path = androidx.compose.ui.graphics.Path()
        points.forEachIndexed { index, valeur ->
            val x = index * stepX
            val y = size.height - ((valeur - min) / plage) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = couleur,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )
    }
}
