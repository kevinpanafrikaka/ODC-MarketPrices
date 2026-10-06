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

data class FavorisUiData(
    val produits: List<ProduitAffiche> = emptyList()
)

class FavorisViewModel(
    private val produitRepository: ProduitRepository,
    private val releveePrixRepository: ReleveePrixRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<FavorisUiData>>(UiState.Loading)

    val uiState: StateFlow<UiState<FavorisUiData>> =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                produitRepository.observerFavoris(),
                releveePrixRepository.observerTousLesReleves()
            ) { favoris, tousLesReleves ->
                construireEtat(favoris, tousLesReleves)
            }.collect { data ->
                _uiState.value = if (data.produits.isEmpty()) UiState.Empty else UiState.Success(data)
            }
        }
    }

    private fun construireEtat(
        favoris: List<Produit>,
        tousLesReleves: List<ReleveePrix>
    ): FavorisUiData {

        val prixParProduit = tousLesReleves.groupBy { it.produitId }

        val produitsAffiches = favoris.map { produit ->
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
                historiquePrixGnf = releves.asReversed().map { it.prixGnf },
                estFavori = true
            )
        }

        return FavorisUiData(produits = produitsAffiches)
    }

    fun onToggleFavori(produitId: Long, favori: Boolean) {
        viewModelScope.launch {
            produitRepository.basculerFavori(produitId, favori)
        }
    }
}
