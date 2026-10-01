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
 * Pastille d'affichage de tendance (Hausse/Baisse/Stable).
 * Composant purement visuel : la valeur "tendance" est reçue toute prête,
 * jamais calculée ici (aucune logique métier dans l'interface).
 */
@Composable
fun TendancePill(tendance: String) {
    val (texte, couleur, fond) = when (tendance) {
        "HAUSSE" -> Triple("↑ Hausse", Hausse, HausseFond)
        "BAISSE" -> Triple("↓ Baisse", Baisse, BaisseFond)
        else -> Triple("→ Stable", Stable, StableFond)
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

/** Couleur neutre pour un avatar/icône qui ne dépend pas de la tendance. */
@Composable
fun couleurAvatarNeutre() = Stable to StableFond