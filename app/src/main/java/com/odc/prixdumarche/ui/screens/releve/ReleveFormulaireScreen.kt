package com.odc.prixdumarche.ui.screens.releve

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
// formulaire de saisie (produit, marché, prix GNF, date) + validations.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleveFormulaireScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Nouveau relevé") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("TODO : formulaire de relevé de prix")
        }
    }
}
