package com.odc.prixdumarche.ui.screens.produit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO(Responsable interface + Responsable logique métier) :
// courbe d'évolution 30 jours + tableau comparatif par marché (min/max/moyenne).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduitDetailScreen(produitId: Long) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Détail produit") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("TODO : détail du produit #$produitId")
        }
    }
}
