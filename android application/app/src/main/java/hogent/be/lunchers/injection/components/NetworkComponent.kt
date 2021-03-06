package hogent.be.lunchers.injection.components

import hogent.be.lunchers.injection.modules.NetworkModule
import hogent.be.lunchers.viewmodels.LunchViewModel
import dagger.Component
import hogent.be.lunchers.viewmodels.AccountViewModel
import hogent.be.lunchers.viewmodels.OrderViewModel
import hogent.be.lunchers.viewmodels.ReservationViewModel
import javax.inject.Singleton

/**
 * Deze [NetworkComponent] dient als tussenlaag tussen de [NetworkModule] en de effectieve [LunchViewModel]
 *
 * Momenteel compatibel met: [LunchViewModel]
 */
@Singleton
/**
 * We hebben de netwerkmodule nodig voor het ophalen van de data
 */
@Component(modules = [NetworkModule::class])
interface NetworkComponent {

    /**
     * Doet dependency injection op de meegegeven LunchViewModel
     *
     * @param lunchViewModel De [LunchViewModel] dat je wilt voorzien van dependency injection. Verplicht van type [LunchViewModel].
     */
    fun inject(lunchViewModel: LunchViewModel)

    /**
     * Doet dependency injection op de meegegeven [AccountViewModel]
     *
     * @param accountViewModel De [AccountViewModel] dat je wilt voorzien van dependency injection. Verplicht van type [AccountViewModel].
     */
    fun inject(accountViewModel: AccountViewModel)

    /**
     * Doet dependency injection op de meegegeven [ReservationViewModel]
     *
     * @param reservationViewModel De [ReservationViewModel] dat je wilt voorzien van dependency injection. Verplicht van type [ReservationViewModel].
     */
    fun inject(reservationViewModel: ReservationViewModel)

    /**
     * Doet dependency injection op de meegegeven [OrderViewModel]
     *
     * @param orderViewModel De [OrderViewModel] dat je wilt voorzien van dependency injection. Verplicht van type [OrderViewModel].
     */
    fun inject(orderViewModel: OrderViewModel)

}