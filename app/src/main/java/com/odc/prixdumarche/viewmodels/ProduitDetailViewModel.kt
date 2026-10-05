package com.odc.prixdumarche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odc.prixdumarche.data.local.entity.ReleveePrix
import com.odc.prixdumarche.data.repository.MarcheRepository
import com.odc.prixdumarche.data.repository.ProduitRepository
import com.odc.prixdumarche.data.repository.ReleveePrixRepository
import com.odc.prixdumarche.domain.Tendance
import com.odc.prixdumarche.domain.model.ComparaisonAffichee
import com.odc.prixdumarche.domain.model.PointCourbe
import com.odc.prixdumarche.domain.model.PrixMarcheAffiche
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProduitDetailUiData(
    val nomProduit: String,
    val unite: String,
    val tendance: Tendance?,
    val historique: List<PointCourbe>,
    val prixParMarche: List<PrixMarcheAffiche>,
    val comparaison: ComparaisonAffichee?
)

class ProduitDetailViewModel(
    private val produitId: Long,
    private val produitRepository: ProduitRepository,
    private val marcheRepository: MarcheRepository,
    private val releveePrixRepository: ReleveePrixRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<ProduitDetailUiData>>(UiState.Loading)

    val uiState: StateFlow<UiState<ProduitDetailUiData>> =
        _uiState.asStateFlow()

    init {
        observerDonnees()
    }

    private fun observerDonnees() {
        viewModelScope.launch {
            combine(
                produitRepository.observerProduit(produitId),
                releveePrixRepository.observerHistorique30Jours(produitId),
                releveePrixRepository.observerDernierPrixParMarche(produitId),
                marcheRepository.observerMarches()
            ) { produit, historique, prixParMarche, marches ->

                if (produit == null) {
                    null
                } else {
                    construireEtat(
                        produit.nom,
                        produit.unite,
                        historique,
                        prixParMarche,
                        marches
                    )
                }

            }.collect { data ->

                if (data == null) {
                    _uiState.value =
                        UiState.Error("Produit introuvable")
                } else {
                    _uiState.value = UiState.Success(data)
                }
            }
        }
    }

    private fun construireEtat(
        nomProduit: String,
        unite: String,
        historique: List<ReleveePrix>,
        prixParMarche: List<ReleveePrix>,
        marches: List<com.odc.prixdumarche.data.local.entity.Marche>
    ): ProduitDetailUiData {

        val historiqueTrie = historique.sortedBy { it.date }

        val points = historiqueTrie.mapIndexed { index, releve ->
            PointCourbe(
                jour = index + 1,
                prixGnf = releve.prixGnf
            )
        }

        val tendance = calculerTendance(historiqueTrie)

        val marcheParId = marches.associateBy { it.id }

        val prixAffiches = prixParMarche
            .sortedBy { it.date }
            .mapNotNull { releve ->

                val marche = marcheParId[releve.marcheId]
                    ?: return@mapNotNull null

                PrixMarcheAffiche(
                    marcheId = marche.id,
                    marche = marche.nom,
                    commune = marche.commune,
                    date = formaterDate(releve.date),
                    prixGnf = releve.prixGnf
                )
            }

        val comparaison = calculerComparaison(
            prixParMarche = prixParMarche,
            marches = marcheParId
        )

        return ProduitDetailUiData(
            nomProduit = nomProduit,
            unite = unite,
            tendance = tendance,
            historique = points,
            prixParMarche = prixAffiches,
            comparaison = comparaison
        )
    }

    private fun calculerTendance(
        releves: List<ReleveePrix>
    ): Tendance? {

        if (releves.size < 2) {
            return null
        }

        val precedent = releves[releves.lastIndex - 1].prixGnf
        val dernier = releves.last().prixGnf

        return when {
            dernier > precedent -> Tendance.HAUSSE
            dernier < precedent -> Tendance.BAISSE
            else -> Tendance.STABLE
        }
    }

    private fun calculerComparaison(
        prixParMarche: List<ReleveePrix>,
        marches: Map<Long, com.odc.prixdumarche.data.local.entity.Marche>
    ): ComparaisonAffichee? {

        if (prixParMarche.isEmpty()) {
            return null
        }

        val moinsCher = prixParMarche.minByOrNull { it.prixGnf }
            ?: return null

        val plusCher = prixParMarche.maxByOrNull { it.prixGnf }
            ?: return null

        val moyenne = prixParMarche
            .map { it.prixGnf }
            .average()
            .toLong()

        val marcheMoinsCher = marches[moinsCher.marcheId]
            ?: return null

        val marchePlusCher = marches[plusCher.marcheId]
            ?: return null

        return ComparaisonAffichee(
            moinsCherMarcheId = marcheMoinsCher.id,
            moinsCherMarche = marcheMoinsCher.nom,
            moinsCherCommune = marcheMoinsCher.commune,
            moinsCherPrixGnf = moinsCher.prixGnf,
            plusCherMarcheId = marchePlusCher.id,
            plusCherMarche = marchePlusCher.nom,
            plusCherCommune = marchePlusCher.commune,
            plusCherPrixGnf = plusCher.prixGnf,
            moyenneGnf = moyenne
        )
    }

    private fun formaterDate(timestamp: Long): String {
        return SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date(timestamp))
    }
}