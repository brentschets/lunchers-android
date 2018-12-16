package hogent.be.lunchers.viewmodels

import android.arch.lifecycle.MutableLiveData
import hogent.be.lunchers.bases.InjectedViewModel
import hogent.be.lunchers.enums.FilterEnum
import hogent.be.lunchers.models.Lunch
import hogent.be.lunchers.networks.LunchersApi
import hogent.be.lunchers.utils.MessageUtil
import hogent.be.lunchers.utils.PreferenceUtil
import hogent.be.lunchers.utils.SearchUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Een [InjectedViewModel] klasse die alle lunches bevat.
 */
class LunchViewModel : InjectedViewModel() {

    /**
     * De lijst van alle lunches die voldoen aan de zoekfilter
     */
    val filteredLunches = MutableLiveData<List<Lunch>>()

    /**
     * De geselecteerde lunch
     */
    val selectedLunch = MutableLiveData<Lunch>()

    /**
     * De lijst van alle lunches zoals die van de server gehaald is
     */
    private var allLunches = listOf<Lunch>()

    /**
     * De longitude waarop user lunches wilt
     */
    private var longitude: Double = 0.00

    /**
     * De latitude waarop user lunches wilt
     */
    private var latitude: Double = 0.00

    /**
     * De geselecteerde filter methode
     */
    var selectedFilter: FilterEnum
        private set

    /**
     * een instantie van de lunchersApi om data van de server op te halen
     */
    @Inject
    lateinit var lunchersApi: LunchersApi

    /**
     * De subscription op het get all lunches verzoek
     */
    private lateinit var getAllLunchesSubscription: Disposable

    init {
        //initieel vullen met een lege lijst zodat dit niet nul os
        filteredLunches.value = emptyList()
        selectedFilter = PreferenceUtil.getDefaultFilterMethod()
        refreshLunches()
    }

    /**
     * Functie voor het behandelen van het mislukken van het ophalen van data van de server
     */
    private fun onRetrieveError(error: Throwable) {
        //voorlopig harde crash, niet goed want throwt dus bij gewoon geen internet etc
        MessageUtil.showToast("Er is iets foutgegaan met het ophalen van de data")
    }

    /**
     * Functie voor het behandelen van het starten van een rest api call
     *
     * Zal een loading fragment tonen of dergelijke
     */
    private fun onRetrieveStart() {
        //hier begint api call
        //nog een soort loading voozien
    }

    /**
     * Functie voor het behandelen van het eindigen van een rest api call
     *
     * Sluit het loading fragment of dergelijke
     */
    private fun onRetrieveFinish() {
        //hier eindigt api call
        //de loading hier nog stoppen
    }

    /**
     * Functie voor het behandelen van het succesvol ophalen van de meetings
     *
     * Zal de lijst van meetings gelijkstellen met het results
     */
    private fun onRetrieveAllLunchesSuccess(result: List<Lunch>) {
        allLunches = result
        filteredLunches.value = SearchUtil.filterLunch(selectedFilter, result)
    }

    /**
     * Disposed alle subscriptions wanneer de [LunchViewModel] niet meer gebruikt wordt.
     */
    override fun onCleared() {
        super.onCleared()
        getAllLunchesSubscription.dispose()
    }

    /**
     * Lunches opnieuw ophalen om te refreshen
     */
    fun refreshLunches() {
        if (selectedFilter == FilterEnum.DISTANCE)
            refreshLunchesFromLocation()
        else
            refreshLunchesDefault()
    }

    /**
     * zoekt met searchstring op name, beschrijvng, ingredienten en tags
     */
    fun search(searchString: String) {
        filteredLunches.value = SearchUtil.searchLunch(searchString, allLunches)
    }

    /**
     * stelt de filtered type in en updat de lijst
     */
    fun setSelectedFilter(filterEnum: FilterEnum, latitude: Double = 0.00, longitude: Double = 0.00) {
        selectedFilter = filterEnum
        if (filterEnum == FilterEnum.DISTANCE) {
            this.longitude = longitude!!
            this.latitude = latitude!!
            refreshLunchesFromLocation()
        } else {
            filteredLunches.value = SearchUtil.filterLunch(selectedFilter, allLunches)
        }
    }

    /**
     * returnt de lijst van alle lunches als MutableLiveData
     */
    fun setSelectedLunch(lunchId: Int) {
        selectedLunch.value = allLunches.firstOrNull { it.lunchId == lunchId }
    }

    /**
     * Lunches opnieuw ophalen om te refreshen
     */
    private fun refreshLunchesFromLocation() {
        getAllLunchesSubscription = lunchersApi.getAllLunchesFromLocation(latitude, longitude)
            //we tell it to fetch the data on background by
            .subscribeOn(Schedulers.io())
            //we like the fetched data to be displayed on the MainTread (UI)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveStart() }
            .doOnTerminate { onRetrieveFinish() }
            .subscribe(
                { result ->
                    run {
                        selectedFilter = FilterEnum.DISTANCE
                        onRetrieveAllLunchesSuccess(result)
                    }
                },
                { error -> onRetrieveError(error) }
            )
    }

    /**
     * Lunches opnieuw ophalen om te refreshen
     */
    private fun refreshLunchesDefault() {
        getAllLunchesSubscription = lunchersApi.getAllLunches()
            //we tell it to fetch the data on background by
            .subscribeOn(Schedulers.io())
            //we like the fetched data to be displayed on the MainTread (UI)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveStart() }
            .doOnTerminate { onRetrieveFinish() }
            .subscribe(
                { result -> onRetrieveAllLunchesSuccess(result) },
                { error -> onRetrieveError(error) }
            )
    }

}