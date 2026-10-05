package com.odc.prixdumarche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odc.prixdumarche.data.local.entity.Produit
import com.odc.prixdumarche.data.local.entity.ReleveePrix
import com.odc.prixdumarche.data.local.entity.Marche
import com.odc.prixdumarche.data.repository.MarcheRepository
import com.odc.prixdumarche.data.repository.ProduitRepository
import com.odc.prixdumarche.data.repository.ReleveePrixRepository
import com.odc.prixdumarche.domain.Tendance
import com.odc.prixdumarche.domain.model.MarcheAffiche
import com.odc.prixdumarche.domain.model.ProduitAffiche
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProduitListUiData(
    val produits: List<ProduitAffiche> = emptyList(),
    val categories: List<String> = emptyList(),
    val marches: List<MarcheAffiche> = emptyList(),
    val categorieFiltre: String = "Toutes",
    val marcheFiltre: Long? = null
)

class ProduitListViewModel(
    private val produitRepository: ProduitRepository,
    private val marcheRepository: MarcheRepository,
    private val releveePrixRepository: ReleveePrixRepository
) : ViewModel() {

    private val categorieFiltre = MutableStateFlow("Toutes")
    private val marcheFiltre = MutableStateFlow<Long?>(null)

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
                marcheRepository.observerMarches(),
                releveePrixRepository.observerDerniersPrixParProduit(),
                categorieFiltre,
                marcheFiltre
            ) { produits, marches, derniersPrix, categorie, marcheId ->

                construireEtat(
                    produits,
                    marches,
                    derniersPrix,
                    categorie,
                    marcheId
                )

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
        marches: List<Marche>,
        derniersPrix: List<ReleveePrix>,
        categorie: String,
        marcheId: Long?
    ): ProduitListUiData {

        val prixParProduit = derniersPrix.groupBy { it.produitId }

        val categories = listOf("Toutes") +
                produits
                    .map { it.categorie }
                    .distinct()
                    .sorted()

        val marchesAffiches = marches.map {
            MarcheAffiche(
                id = it.id,
                nom = it.nom,
                commune = it.commune
            )
        }

        val produitsAffiches = produits
            .filter {
                categorie == "Toutes" || it.categorie == categorie
            }
            .mapNotNull { produit ->

                val releves = prixParProduit[produit.id]
                    .orEmpty()
                    .sortedByDescending { it.date }

                val dernier = releves.firstOrNull()

                if (marcheId != null && dernier?.marcheId != marcheId) {
                    return@mapNotNull null
                }

                ProduitAffiche(
                    id = produit.id,
                    nom = produit.nom,
                    unite = produit.unite,
                    categorie = produit.categorie,
                    dernierPrixGnf = dernier?.prixGnf,
                    tendance = calculerTendance(releves).name
                )
            }

        return ProduitListUiData(
            produits = produitsAffiches,
            categories = categories,
            marches = marchesAffiches,
            categorieFiltre = categorie,
            marcheFiltre = marcheId
        )
    }

    private fun calculerTendance(
        releves: List<ReleveePrix>
    ): Tendance {

        if (releves.size < 2) {
            return Tendance.STABLE
        }

        val dernier = releves[0].prixGnf
        val precedent = releves[1].prixGnf

        return when {
            dernier > precedent -> Tendance.HAUSSE
            dernier < precedent -> Tendance.BAISSE
            else -> Tendance.STABLE
        }
    }

    fun onCategorieChoisie(categorie: String) {
        categorieFiltre.value = categorie
    }

    fun onMarcheChoisi(marcheId: Long?) {
        marcheFiltre.value = marcheId
    }
}