package com.odc.prixdumarche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odc.prixdumarche.data.local.entity.Produit
import com.odc.prixdumarche.data.repository.ProduitRepository
import com.odc.prixdumarche.data.repository.ReleveePrixRepository
import com.odc.prixdumarche.domain.Tendance
import com.odc.prixdumarche.domain.model.VariationAffichee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine

data class TableauBordUiData(
    val panierMoyenGnf: Long?,
    val nbProduitsPanier: Int,
    val hausses: List<VariationAffichee>,
    val baisses: List<VariationAffichee>
)

class TableauBordViewModel(
    private val produitRepository: ProduitRepository,
    private val releveePrixRepository: ReleveePrixRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<TableauBordUiData>>(UiState.Loading)

    val uiState: StateFlow<UiState<TableauBordUiData>> =
        _uiState.asStateFlow()

    init {
        observerProduits()
    }

    private fun observerProduits() {

        viewModelScope.launch {

            produitRepository
                .observerProduits()
                .collect { produits ->

                    if (produits.isEmpty()) {
                        _uiState.value = UiState.Empty
                        return@collect
                    }

                    calculerTableauDeBord(produits)
                }
        }
    }

    private fun calculerTableauDeBord(
        produits: List<Produit>
    ) {

        viewModelScope.launch {

            val historiques = produits.map { produit ->

                produit.id to
                        releveePrixRepository
                            .observerHistorique30Jours(produit.id)
            }

            combine(
                historiques.map { it.second }
            ) { tableaux ->

                produits.mapIndexed { index, produit ->

                    produit to tableaux[index]
                }

            }.collect { donnees ->

                val variations = donnees.mapNotNull {
                    calculerVariation(it.first, it.second)
                }

                val derniersPrix = donnees.mapNotNull { (_, releves) ->
                    releves.maxByOrNull { it.date }?.prixGnf
                }

                val panierMoyen =
                    if (derniersPrix.isEmpty()) {
                        null
                    } else {
                        derniersPrix.average().toLong()
                    }

                val hausses = variations
                    .filter { it.tendance == Tendance.HAUSSE.name }
                    .sortedByDescending { it.pourcentage }

                val baisses = variations
                    .filter { it.tendance == Tendance.BAISSE.name }
                    .sortedBy { it.pourcentage }

                val data = TableauBordUiData(
                    panierMoyenGnf = panierMoyen,
                    nbProduitsPanier = derniersPrix.size,
                    hausses = hausses,
                    baisses = baisses
                )

                if (
                    panierMoyen == null &&
                    hausses.isEmpty() &&
                    baisses.isEmpty()
                ) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success(data)
                }
            }
        }
    }

    private fun calculerVariation(
        produit: Produit,
        releves: List<com.odc.prixdumarche.data.local.entity.ReleveePrix>
    ): VariationAffichee? {

        val tries = releves.sortedBy { it.date }

        if (tries.size < 2) {
            return null
        }

        val precedent = tries[tries.lastIndex - 1].prixGnf
        val dernier = tries.last().prixGnf

        if (precedent <= 0) {
            return null
        }

        val difference = dernier - precedent

        val pourcentage = (
                difference.toDouble() /
                        precedent.toDouble() *
                        100
                ).toInt()

        val tendance = when {
            dernier > precedent -> Tendance.HAUSSE
            dernier < precedent -> Tendance.BAISSE
            else -> Tendance.STABLE
        }

        if (tendance == Tendance.STABLE) {
            return null
        }

        return VariationAffichee(
            nomProduit = produit.nom,
            prixGnf = dernier,
            pourcentage = kotlin.math.abs(pourcentage),
            tendance = tendance.name
        )
    }
}