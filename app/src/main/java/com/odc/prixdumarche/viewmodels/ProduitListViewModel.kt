package com.odc.prixdumarche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odc.prixdumarche.data.local.entity.Produit
import com.odc.prixdumarche.data.local.entity.ReleveePrix
import com.odc.prixdumarche.data.repository.ProduitRepository
import com.odc.prixdumarche.data.repository.ReleveePrixRepository
import com.odc.prixdumarche.domain.model.ProduitAffiche
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProduitListUiData(
    val produits: List<ProduitAffiche> = emptyList(),
    val categories: List<String> = emptyList(),
    val categorieFiltre: String = "Toutes"
)

class ProduitListViewModel(
    private val produitRepository: ProduitRepository,
    private val releveePrixRepository: ReleveePrixRepository
) : ViewModel() {

    private val categorieFiltre = MutableStateFlow("Toutes")

    private val _uiState =
        MutableStateFlow<UiState<ProduitListUiData>>(UiState.Loading)

    val uiState: StateFlow<UiState<ProduitListUiData>> =
        _uiState.asStateFlow()

    init {
        observerDonnees()
    }

    private fun observerDonnees() {
        viewModelScope.launch {
            combine(
                produitRepository.observerProduits(),
                releveePrixRepository.observerTousLesReleves(),
                categorieFiltre
            ) { produits, tousLesReleves, categorie ->

                construireEtat(produits, tousLesReleves, categorie)

            }.collect { data ->

                if (data.produits.isEmpty() && data.categories.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success(data)
                }
            }
        }
    }

    private fun construireEtat(
        produits: List<Produit>,
        tousLesReleves: List<ReleveePrix>,
        categorie: String
    ): ProduitListUiData {

        val prixParProduit = tousLesReleves.groupBy { it.produitId }

        val categories = listOf("Toutes") +
                produits
                    .map { it.categorie }
                    .distinct()
                    .sorted()

        val produitsAffiches = produits
            .filter {
                categorie == "Toutes" || it.categorie == categorie
            }
            .map { produit ->

                val releves = prixParProduit[produit.id]
                    .orEmpty()
                    .sortedByDescending { it.date }

                ProduitAffiche(
                    id = produit.id,
                    nom = produit.nom,
                    unite = produit.unite,
                    categorie = produit.categorie,
                    dernierPrixGnf = releves.firstOrNull()?.prixGnf,
                    tendance = calculerTendance(releves).name,
                    historiquePrixGnf = releves.asReversed().map { it.prixGnf }
                )
            }

        return ProduitListUiData(
            produits = produitsAffiches,
            categories = categories,
            categorieFiltre = categorie
        )
    }

    fun onCategorieChoisie(categorie: String) {
        categorieFiltre.value = categorie
    }
}
