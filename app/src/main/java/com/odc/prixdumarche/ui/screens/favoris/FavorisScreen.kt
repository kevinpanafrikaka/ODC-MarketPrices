package com.odc.prixdumarche.ui.screens.favoris

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.domain.model.ProduitAffiche
import com.odc.prixdumarche.ui.components.CarteProduitGrille
import com.odc.prixdumarche.ui.theme.PrixDuMarcheTheme

/**
 * Écran Favoris (onglet bas). Montre les produits marqués favoris depuis la
 * liste ou cet écran lui-même — même carte, même toggle cœur.
 */
@Composable
fun FavorisScreen(
    produits: List<ProduitAffiche> = emptyList(),
    chargement: Boolean = false,
    onProduitClick: (Long) -> Unit = {},
    onToggleFavori: (Long, Boolean) -> Unit = { _, _ -> }
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text("Favoris", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        if (!chargement && produits.isEmpty()) {
            Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun favori pour le moment", fontSize = 16.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(produits, key = { it.id }) { p ->
                    CarteProduitGrille(
                        p = p,
                        onClick = { onProduitClick(p.id) },
                        onToggleFavori = { favori -> onToggleFavori(p.id, favori) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavorisScreenPreview() {
    PrixDuMarcheTheme {
        FavorisScreen(
            produits = listOf(
                ProduitAffiche(1, "Riz local", "kg", "Céréales", 9_600, "HAUSSE", estFavori = true)
            )
        )
    }
}
