package com.odc.prixdumarche.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.ui.theme.*

/**
 * Pastille d'affichage de tendance (Hausse/Baisse/Stable), avec un pourcentage
 * optionnel (ex. "+4 %"). Composant purement visuel : tendance et pourcentage
 * sont reçus tout prêts, jamais calculés ici (aucune logique métier dans l'interface).
 */
@Composable
fun TendancePill(tendance: String, pourcentage: Int? = null) {
    val (fleche, couleur, fond) = when (tendance) {
        "HAUSSE" -> Triple("↑", Hausse, HausseFond)
        "BAISSE" -> Triple("↓", Baisse, BaisseFond)
        else -> Triple("→", Stable, StableFond)
    }
    val texte = when {
        pourcentage != null && tendance == "HAUSSE" -> "$fleche +$pourcentage %"
        pourcentage != null && tendance == "BAISSE" -> "$fleche $pourcentage %"
        pourcentage != null -> "$fleche $pourcentage %"
        tendance == "HAUSSE" -> "$fleche Hausse"
        tendance == "BAISSE" -> "$fleche Baisse"
        else -> "$fleche Stable"
    }
    Surface(color = fond, shape = RoundedCornerShape(999.dp)) {
        Text(
            texte,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = couleur,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}