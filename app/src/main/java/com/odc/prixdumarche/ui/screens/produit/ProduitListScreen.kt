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
// brancher un ProduitListViewModel (StateFlow) exposant la liste des produits
// avec dernier prix, tendance et filtres — voir cahier des charges "Écrans attendus".
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduitListScreen(onProduitClick: (Long) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Produits") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("TODO : liste des produits")
        }
    }
}
