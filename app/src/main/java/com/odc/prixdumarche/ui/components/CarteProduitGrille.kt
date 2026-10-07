package com.odc.prixdumarche.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.domain.model.ProduitAffiche
import com.odc.prixdumarche.ui.theme.Stable
import com.odc.prixdumarche.ui.theme.StableFond
import com.odc.prixdumarche.ui.util.categorieIcon
import com.odc.prixdumarche.ui.util.enGnf
import com.odc.prixdumarche.ui.util.produitImageRes

/**
 * Carte produit au format portrait (9:16) pour une grille à 2 colonnes :
 * image en haut (ou icône de catégorie en repli tant que l'image n'existe
 * pas), nom/unité/prix/tendance en dessous.
 */
@Composable
fun CarteProduitGrille(
    p: ProduitAffiche,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageRes = remember(p.nom) { produitImageRes(context, p.nom) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth().weight(1f)) {
                if (imageRes != null) {
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = p.nom,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier.fillMaxSize().background(StableFond),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            categorieIcon(p.categorie),
                            contentDescription = p.categorie,
                            tint = Stable,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
            Column(Modifier.fillMaxWidth().padding(10.dp)) {
                Text(
                    p.nom,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "par ${p.unite}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        p.dernierPrixGnf?.enGnf() ?: "—",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(Modifier.padding(top = 6.dp)) {
                    TendancePill(p.tendance)
                }
            }
        }
    }
}
