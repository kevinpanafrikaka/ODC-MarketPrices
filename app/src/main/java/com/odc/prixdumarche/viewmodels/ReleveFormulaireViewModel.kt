package com.odc.prixdumarche.viewmodels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odc.prixdumarche.data.local.entity.ReleveePrix
import com.odc.prixdumarche.data.repository.MarcheRepository
import com.odc.prixdumarche.data.repository.ProduitRepository
import com.odc.prixdumarche.data.repository.ReleveePrixRepository
import com.odc.prixdumarche.domain.model.MarcheChoix
import com.odc.prixdumarche.domain.model.OptionChoix
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

data class ReleveFormulaireUiData(
    val produits: List<OptionChoix> = emptyList(),
    val marches: List<MarcheChoix> = emptyList(),
    val produitChoisi: OptionChoix? = null,
    val marcheChoisi: MarcheChoix? = null,
    val prixTexte: String = "",
    val dateTexte: String = "",
    val erreurProduit: String? = null,
    val erreurMarche: String? = null,
    val erreurPrix: String? = null,
    val erreurDate: String? = null,
    val enregistrementReussi: Boolean = false
)

class ReleveFormulaireViewModel(
    private val produitRepository: ProduitRepository,
    private val marcheRepository: MarcheRepository,
    private val releveePrixRepository: ReleveePrixRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<ReleveFormulaireUiData>>(UiState.Loading)

    val uiState: StateFlow<UiState<ReleveFormulaireUiData>> =
        _uiState.asStateFlow()

    private var produits = emptyList<OptionChoix>()
    private var marches = emptyList<MarcheChoix>()

    private var produitChoisi: OptionChoix? = null
    private var marcheChoisi: MarcheChoix? = null
    private var prixTexte = ""
    private var dateTexte = ""

    private var erreurProduit: String? = null
    private var erreurMarche: String? = null
    private var erreurPrix: String? = null
    private var erreurDate: String? = null

    init {
        observerOptions()
    }

    private fun observerOptions() {
        viewModelScope.launch {
            combine(
                produitRepository.observerProduits(),
                marcheRepository.observerMarches()
            ) { produitsRoom, marchesRoom ->

                produits = produitsRoom.map {
                    OptionChoix(
                        id = it.id,
                        label = it.nom
                    )
                }

                marches = marchesRoom.map {
                    MarcheChoix(
                        id = it.id,
                        nom = it.nom,
                        commune = it.commune
                    )
                }

                construireEtat()

            }.collect {
                _uiState.value = UiState.Success(it)
            }
        }
    }

    fun onProduitChoisi(option: OptionChoix) {
        produitChoisi = option
        erreurProduit = null
        publierEtat()
    }

    fun onMarcheChoisi(marche: MarcheChoix) {
        marcheChoisi = marche
        erreurMarche = null
        publierEtat()
    }

    fun onPrixChange(texte: String) {
        prixTexte = texte
        erreurPrix = null
        publierEtat()
    }

    fun onDateChange(texte: String) {
        dateTexte = texte
        erreurDate = null
        publierEtat()
    }

    fun onEnregistrer() {

        erreurProduit = null
        erreurMarche = null
        erreurPrix = null
        erreurDate = null

        var valide = true

        if (produitChoisi == null) {
            erreurProduit = "Veuillez choisir un produit."
            valide = false
        }

        if (marcheChoisi == null) {
            erreurMarche = "Veuillez choisir un marché."
            valide = false
        }

        val prix = prixTexte.trim().toLongOrNull()

        if (prix == null || prix <= 0) {
            erreurPrix = "Le prix doit être supérieur à 0."
            valide = false
        }

        val date = convertirDate(dateTexte)

        if (date == null) {
            erreurDate = "La date doit être au format jj/mm/aaaa."
            valide = false
        }

        publierEtat()

        if (!valide) {
            return
        }

        viewModelScope.launch {

            val releve = ReleveePrix(
                produitId = produitChoisi!!.id,
                marcheId = marcheChoisi!!.id,
                prixGnf = prix!!,
                date = date!!
            )

            try {
                releveePrixRepository.ajouter(releve)

                // On vide le formulaire après succès.
                produitChoisi = null
                marcheChoisi = null
                prixTexte = ""
                dateTexte = ""

                _uiState.value = UiState.Success(construireEtat().copy(enregistrementReussi = true))

            } catch (e: Exception) {

                _uiState.value =
                    UiState.Error(
                        "Impossible d'enregistrer le relevé."
                    )
            }
        }
    }

    private fun convertirDate(texte: String): Long? {

        if (texte.isBlank()) {
            return null
        }

        val formatter = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        formatter.isLenient = false

        return try {
            formatter.parse(texte)?.time
        } catch (e: ParseException) {
            null
        }
    }

    private fun construireEtat(): ReleveFormulaireUiData {
        return ReleveFormulaireUiData(
            produits = produits,
            marches = marches,
            produitChoisi = produitChoisi,
            marcheChoisi = marcheChoisi,
            prixTexte = prixTexte,
            dateTexte = dateTexte,
            erreurProduit = erreurProduit,
            erreurMarche = erreurMarche,
            erreurPrix = erreurPrix,
            erreurDate = erreurDate
        )
    }

    private fun publierEtat() {
        _uiState.value = UiState.Success(construireEtat())
    }
}