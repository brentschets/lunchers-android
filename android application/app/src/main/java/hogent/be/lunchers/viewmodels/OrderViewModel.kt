package hogent.be.lunchers.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import hogent.be.lunchers.bases.InjectedViewModel
import hogent.be.lunchers.models.Reservation
import hogent.be.lunchers.repositories.ReservatieRepository
import hogent.be.lunchers.networks.LunchersApi
import hogent.be.lunchers.utils.MessageUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class OrderViewModel : InjectedViewModel() {

    @Inject
    lateinit var lunchersApi: LunchersApi

    @Inject
    lateinit var orderRepo: ReservatieRepository

    private val _reservations = MutableLiveData<List<Reservation>>()

    val reservations: MutableLiveData<List<Reservation>>
        get() = _reservations

    private var _selectedOrder = MutableLiveData<Reservation>()

    val selectedOrder: MutableLiveData<Reservation>
        get() = _selectedOrder

    private var _roomOrders: LiveData<List<Reservation>>

    val roomOrders: LiveData<List<Reservation>>
        get() = _roomOrders

    private var getAllReservationsSubscription: Disposable

    init {
        _reservations.value = emptyList()

        _selectedOrder.value = null

        _roomOrders = orderRepo.orders

        getAllReservationsSubscription = lunchersApi.getAllOrders()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> onRetrieveAllReservationsSuccess(result) },
                { onRetrieveError() }
            )
    }

    override fun onCleared() {
        super.onCleared()
        getAllReservationsSubscription.dispose()
    }

    fun resetViewModel() {
        _reservations.value = emptyList()

        _selectedOrder.value = null

        _roomOrders = orderRepo.orders

        getAllReservationsSubscription = lunchersApi.getAllOrders()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> onRetrieveAllReservationsSuccess(result) },
                { onRetrieveError() }
            )
    }

    fun setSelectedOrder(orderId: Int) { _selectedOrder.value =  _reservations.value!!.firstOrNull { it.reservationId == orderId } }

    fun setReservations(reservations: List<Reservation>) { _reservations.value = reservations }

    private fun onRetrieveAllReservationsSuccess(result: List<Reservation>) {
        setReservations(result)
        doAsync { orderRepo.insert(result) }
    }

    private fun onRetrieveError() { MessageUtil.showToast("Er is een fout opgetreden tijdens het ophalen van de reservations van het internet.") }

}
